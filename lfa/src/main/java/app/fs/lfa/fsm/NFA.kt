package app.fs.lfa.fsm

class NFA<E,Q>{

    private var startState : Node<E,Q>? = null
    private var currentState : MutableSet<Node<E,Q>?> = mutableSetOf()
    private var states : HashMap<Q, Node<E,Q>> = HashMap()
    private var alphabet : MutableSet<E> = mutableSetOf()
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

    fun setupTransitions(t : List<TransitionData<E,Q>>){
        t.forEach {
            if(states[it.from]?.transitions?.containsKey(it.symbol) == true){
                states[it.from]?.transitions?.get(it.symbol)?.add(it.to)
            }else{
                states[it.from]?.transitions?.set(it.symbol, mutableSetOf(it.to))
            }
        }
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

        val newCurrentState = mutableSetOf<Node<E,Q>?>()

        currentState.forEach { node ->
            fromToPair.add(Pair(node?.stateLabel, mutableListOf()))
            val v = node?.transit(inputString[inputIndex])
            v?.let {
                it.forEach { str ->
                    fromToPair.last().second.add(str)
                    newCurrentState.add(states[str])
                }
            }
        }
        inputIndex++
        currentState = newCurrentState

        return CurrentStateData(inputIndex < inputString.size, oldInput, fromToPair)
    }

    fun addInput(i : List<E>) {
        inputString = i.toMutableList()
    }

    fun isOnAcceptState() = currentState.any { acceptStates.contains(it?.stateLabel) }

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


    class Node<E,Q>(val stateLabel : Q? = null){

        var transitions : HashMap<E, MutableSet<Q>> = HashMap()

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
    val nfa = NFA<String,String>()
    nfa.addStates("q1,q2,q3,q4".split(','))
    nfa.setStartState("q1")
    nfa.addAlphabet("0,1".split(","))
    nfa.addAcceptStates(listOf("q4"))

    val transitions = listOf(
        NFA.TransitionData("q1", "0", "q1"),
        NFA.TransitionData("q1", "1", "q1"),
        NFA.TransitionData("q1", "1", "q2"),
        NFA.TransitionData("q2", "0", "q3"),
        NFA.TransitionData("q2", "1", "q3"),
        NFA.TransitionData("q3", "0", "q4"),
        NFA.TransitionData("q3", "1", "q4"))

    nfa.setupTransitions(transitions)

    val str = nfa.getVizFormat()

    nfa.addInput(listOf("0","1","0","1"))

    while(true){
        val dataResult = nfa.nextStep()
        println(dataResult)
        if (dataResult.hasInput.not()) break
    }
    if(nfa.isOnAcceptState()){
        print("accept")
    }else print("reject")

}