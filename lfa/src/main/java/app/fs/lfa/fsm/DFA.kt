package app.fs.lfa.fsm


class DFA<E,Q>{
    var startState : Node<E,Q>? = null
    private var currentState : Node<E,Q>? = Node()
    private var states : HashMap<Q, Node<E,Q>> = HashMap()
    var alphabet : MutableSet<E> = mutableSetOf()
    private var acceptStates : MutableSet<Q> = mutableSetOf()
    private var inputIndex = 0
    private var inputString : MutableList<E> = mutableListOf()
    private var setupFlag = 0
    private var automataStep : AutomataStep<E,Q>? = null

    fun setStartState(s : Q){
        currentState = states[s]
        startState = currentState
        setupFlag = setupFlag or 4
    }

    fun addAcceptStates(a : List<Q>){
        if(a.isNotEmpty()) {
            a.forEach {
                acceptStates.add(it)
            }
            setupFlag = setupFlag or 8
        }
    }

    fun addStates(s : List<Q>){
        if(s.isNotEmpty()) {
            s.forEach {
                states[it] = Node(it)
            }
            setupFlag = setupFlag or 1
        }
    }

    fun addAlphabet(a : List<E>){
        if(a.isNotEmpty()) {
            a.forEach {
                alphabet.add(it)
            }
            setupFlag = setupFlag or 2
        }
    }

    fun setupTransitions(t : List<TransitionData<E,Q>>){
        if(t.isNotEmpty()) {
            t.forEach {
                states[it.from]?.transitions?.set(it.symbol, it.to)
            }
            setupFlag = setupFlag or 16
        }
    }

    fun isAutomatonReady() : Boolean{
        return setupFlag == 31
    }

    fun setIsReady(i : Boolean){ setupFlag = if(i) 31 else setupFlag}

    fun nextStep() : CurrentStateData<E,Q>?{
        if(isAutomatonReady().not() || inputIndex >= inputString.size || inputIndex < 0) return null

        val was = currentState?.stateLabel
        val ir = inputString[inputIndex]
        val lastState = currentState

        //inputIndex++
        val aux = states[currentState?.transit(inputString[inputIndex++])] ?: return CurrentStateData(was, ir, null)

        currentState = aux

        val step = AutomataStep<E,Q>()
        step.state = lastState
        if(automataStep == null){
            automataStep = step
        }else{
            step.previousStep = automataStep
            automataStep = step
        }

        return CurrentStateData(was, ir, currentState?.stateLabel)
    }

    fun hasInput() : Boolean { return inputIndex < inputString.size }

    data class PreviousData<E,Q>(
        var was : Q,
        var now : Q)

    fun previousStep() : PreviousData<E,Q>?{
        if(automataStep != null){
            val was = currentState?.stateLabel!!
            val now = automataStep?.state?.stateLabel!!
            currentState = automataStep?.state
            automataStep = automataStep?.previousStep
            inputIndex--
            return PreviousData(was, now)
        }
        return null
    }

    fun addInput(i : List<E>) {
        inputString = i.toMutableList()
    }

    fun isOnAcceptState() = acceptStates.contains(currentState?.stateLabel)


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
                val pair = Pair(state.key, transition.value)
                if(transStr[pair] == null) transStr[pair] = mutableListOf()
                transStr[pair]?.add(transition.key)
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

    private inner class AutomataStep<E,Q>{
        var previousStep : AutomataStep<E,Q>? = null

        var state : Node<E,Q>? = null
    }

    data class CurrentStateData<E,Q>(
        val whereItWas : Q? = null,
        val symbolRead : E? = null,
        val whereItsNow : Q? = null,
    ){
        override fun toString(): String {
            return "$whereItWas, $symbolRead --> $whereItsNow"
        }
    }

    class Node<E,Q>(val stateLabel : Q? = null){

        var transitions : HashMap<E, Q> = HashMap()

        fun transit(symbol : E) : Q? = transitions[symbol]

        override fun toString(): String {
            return stateLabel.toString()
        }
    }

    data class TransitionData<E,Q>(var from : Q, var symbol : E, var to : Q){
        override fun toString(): String {
            return "$from, $symbol --> $to"
        }
    }

    private fun <T> MutableList<T>.customToString() : String{
        var str : String = ""
        forEach {
            str += "${it.toString()},"
        }
        return str.dropLast(1)
    }
}

fun main(){
    val dfa = DFA<String,String>()
    dfa.addStates("A,B,C".split(","))
    dfa.setStartState("A")
    dfa.addAlphabet("0,1".split(","))
    dfa.addAcceptStates(listOf("B"))

    val transitions = listOf(
        DFA.TransitionData("A", "0", "A"),
        DFA.TransitionData("A", "1", "B"),
        DFA.TransitionData("B", "1", "B"),
        DFA.TransitionData("B", "0", "C"),
        DFA.TransitionData("C", "0", "B"),
        DFA.TransitionData("C", "1", "B"))

    dfa.setupTransitions(transitions)


//    dfa.addInput(listOf("1","1"))
//
//    while(true){
//        val dataResult = dfa.nextStep()
//        println(dataResult)
//        if (dataResult.hasInput.not()) break
//    }
//    if(dfa.isOnAcceptState()){
//        println("accept")
//    }else println("reject")
//
//    println(dfa.getVizFormat())
}