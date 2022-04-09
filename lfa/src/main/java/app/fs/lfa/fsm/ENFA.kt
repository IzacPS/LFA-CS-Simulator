package app.fs.lfa.fsm

class ENFA<E,Q>(var emptySymbol : E){

    private var startState : Node<E,Q>? = null
    private var currentState : MutableSet<Node<E,Q>?> = mutableSetOf()
    private var states : HashMap<Q, Node<E,Q>> = HashMap()
    private var alphabet : MutableSet<E> = mutableSetOf(emptySymbol)
    private var acceptStates : MutableSet<Q> = mutableSetOf()
    private var inputIndex = 0
    private var inputString : MutableList<E> = mutableListOf()

    fun setStartState(s : Q){
        currentState.add(states[s])
        startState = states[s]
    }

    fun addAcceptStates(a : List<Q>){
        a.forEach {
            acceptStates.add(it)
        }
    }

    fun addStates(s : List<Q>){
        s.forEach{
            states[it] = Node(it)
        }
    }

    fun addAlphabet(a : List<E>){
        a.forEach {
            alphabet.add(it)
        }
    }

    fun getAllEmptyTransitions(node : Node<E,Q>?, emptyTransitions : MutableSet<Q>){
        node?.transitions?.get(emptySymbol)?.let { to ->
            to.forEach {
                emptyTransitions.add(it)
                getAllEmptyTransitions(states[it], emptyTransitions)
            }
        }
    }

    fun setupTransitions(t : List<TransitionData<E,Q>>){
        t.forEach {
            if(states[it.from]?.transitions?.containsKey(it.symbol) == true){
                states[it.from]?.transitions?.get(it.symbol)?.add(it.to)
            }else{
                states[it.from]?.transitions?.set(it.symbol, mutableSetOf(it.to))
            }
            states[it.from]?.statesFromEpsilon?.let { t ->
                getAllEmptyTransitions(states[it.from], t)
            }
        }
    }

    fun getVizFormat() : String{
        var vizStr : String = "digraph finite_state_machine { " +
                "fontname=\"Helvetica,Arial,sans-serif\" " +
                "node [fontname=\"Helvetica,Arial,sans-serif\"] " +
                "edge [fontname=\"Helvetica,Arial,sans-serif\"] " +
                "rankdir=LR; " +
                "node [shape = point ]; start; "

        var strAccept : String = ""
        acceptStates.forEach {
            strAccept += "${it.toString()} "
        }

        vizStr += "node [shape = doublecircle]; $strAccept; "

        vizStr += "node [shape = circle]; "

        val transStr : HashMap<Pair<Q,Q>, MutableList<E>> = hashMapOf()

        states.forEach { state->
            state.value.transitions.forEach { transition ->
                transition.value.forEach { q ->
                    val pair = Pair(state.key, q)
                    if(transStr[pair] == null) transStr[pair] = mutableListOf()
                    transStr[pair]?.add(transition.key)
                }
            }
        }

        transStr.forEach {
            vizStr += "${it.key.first.toString()} -> ${it.key.second.toString()} [label = \"${it.value.customToString()}\"]; "
        }

        //if(startStateIsAcceptState.not()){
        vizStr += "start -> ${startState?.stateLabel.toString()}; "
        //}

        vizStr +="}"

        return vizStr
    }


    data class CurrentStateData<E,Q>(
        var hasInput : Boolean,
        val symbolRead : E? = null,
        val fromToPair : MutableList<Pair<Q?, MutableList<Q?>>> = mutableListOf()
    ){
        override fun toString(): String {
            var str : String = ""
            fromToPair.forEach {
                str += "${it.first} , $symbolRead --> ${it.second}\n"
            }
            return str
        }
    }

    fun nextStep() : CurrentStateData<E,Q>{

        val oldInput = inputString[inputIndex]
        val fromToPair = mutableListOf<Pair<Q?,MutableList<Q?>>>()

//        //val newCurrentState = mutableSetOf<NFA.Node<E, Q>?>()
//
//        currentState.forEach { node ->
//            fromToPair.add(Pair(node?.stateLabel, mutableListOf()))
//            val v = node?.transit(inputString[inputIndex])
//            v?.let {
//                it.forEach { str ->
//                    fromToPair.last().second.add(str)
//                    newCurrentState.add(states[str])
//                }
//            }
//        }
//        inputIndex++
//        currentState = newCurrentState
//
//

        startState?.statesFromEpsilon?.forEach {
            currentState.add(states[it])
        }

        val newCurrentState = mutableSetOf<Node<E,Q>?>()

        currentState.forEach { node ->
            fromToPair.add(Pair(node?.stateLabel, mutableListOf()))
            val v = node?.transit(inputString[inputIndex])
            v?.let {
                it.forEach { str ->
                    newCurrentState.add(states[str])
                    fromToPair.last().second.add(str)
                    states[str]?.statesFromEpsilon?.forEach { es ->
                            fromToPair.last().second.add(es)
                            newCurrentState.add(states[es])
                    }
                }
            }
        }
        currentState = newCurrentState
        inputIndex++

        return CurrentStateData(inputIndex < inputString.size, oldInput, fromToPair)
    }

    fun addInput(i : List<E>) {
        inputString = i.toMutableList()
    }

    fun isOnAcceptState() = currentState.any { acceptStates.contains(it?.stateLabel) }

//    fun simulate(input : String) : Boolean{
//        startState?.statesFromEpsilon?.forEach {
//            currentState.add(statesFromInput[it])
//        }
//
//        print("currentStates : ")
//        currentState.forEach { print("${it?.label} ") }
//        println()
//
//        input.forEach { c ->
//            val newCurrentState = mutableSetOf<Node?>()
//            currentState.forEach { node ->
//                val v = node?.transit(c.toString())
//                v?.let {
//                    it.forEach { str ->
//                        newCurrentState.add(statesFromInput[str])
//                        statesFromInput[str]?.statesFromEpsilon?.forEach { es -> newCurrentState.add(statesFromInput[es]) }
//                    }
//                }
//            }
//            currentState = newCurrentState
//            print("currentStates : ")
//            currentState.forEach { print("${it?.label} ") }
//            println()
//        }
//        return currentState.any { acceptStates.contains(it?.label) }
//    }

    class Node<E,Q>(val stateLabel: Q? = null){

        var transitions : HashMap<E, MutableSet<Q>> = HashMap()

        var statesFromEpsilon : MutableSet<Q> = mutableSetOf()

        fun transit(symbol : E) : MutableSet<Q>? = transitions[symbol]
    }

    data class TransitionData<E,Q>(var from : Q, var symbol : E, var to : Q)

    private fun <T> MutableList<T>.customToString() : String{
        var str : String = ""
        forEach {
            str += "${it.toString()},"
        }
        return str.dropLast(1)
    }

}

fun main(){
    val enfa = ENFA<String,String>("ε")
    enfa.addStates("q0,q1,q2,q3,q4,q5,q6,q7".split(","))
    enfa.setStartState("q0")
    enfa.addAlphabet("a,b".split(","))
    enfa.addAcceptStates(listOf("q5,q7"))

    val transitions = listOf(
        ENFA.TransitionData("q0", enfa.emptySymbol, "q1"),
        ENFA.TransitionData("A", enfa.emptySymbol, "C"),
        ENFA.TransitionData("B", "0", "F"),
        ENFA.TransitionData("C", "0", "E"),
        ENFA.TransitionData("D", "0", "C"),
        ENFA.TransitionData("E", "0", "D"),
        ENFA.TransitionData("F", "1", "B"))

    enfa.setupTransitions(transitions)

    val str = enfa.getVizFormat()


    //enfa.addInput(listOf("0","1","0","1","1","0"))

    while(true){
        val dataResult = enfa.nextStep()
        println(dataResult)
        if (dataResult.hasInput.not()) break
    }
    if(enfa.isOnAcceptState()){
        print("accept")
    }else print("reject")
}