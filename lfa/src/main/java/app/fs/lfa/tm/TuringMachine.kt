package app.fs.lfa.tm

class TuringMachine<E,Q>() {
    //Map a string representing a State to the object that has that state Values
    var tape : MutableList<E?> = mutableListOf()

    var states : HashMap<Q, Node<E,Q>> = HashMap()

    var inputAlphabet : MutableSet<E> = mutableSetOf()

    var tapeAlphabet : MutableSet<E> = mutableSetOf()

    var startState : Q? = null
    set(value) {
        currentState = states[value]
        field = currentState?.stateLabel
        setupFlag = setupFlag or 8
    }

    private var currentState : Node<E,Q>? = null

    var acceptState : Q? = null
    set(value) {
        field = value
        setupFlag = setupFlag or 16
    }

    var rejectState : Q? = null
    set(value) {
        field = value
        setupFlag = setupFlag or 32
    }

    var emptySymbol : E? = null
    set(value) {
        field = value
        setupFlag = setupFlag or 64
    }

    private var head : Int = 0
    private var setupFlag = 0

    private var machineStep : MachineStep<E,Q>? = null

    fun addStates(s : List<Q>){
        s.forEach {
            states[it] = Node(it)
        }
        if(s.isNotEmpty()) setupFlag = setupFlag or 1
    }


    fun addInputAlphabet(a : List<E>){
        a.forEach {
            inputAlphabet.add(it)
        }
        if(a.isNotEmpty()) setupFlag = setupFlag or 2
    }

    fun addTapeAlphabet(t : List<E>){
        t.forEach {
            tapeAlphabet.add(it)
        }
        if(t.isNotEmpty()) setupFlag = setupFlag or 4
    }

    fun isMachineReady() : Boolean{
        return setupFlag == 255
    }

    fun setIsReady(i : Boolean){ setupFlag = if(i) 255 else setupFlag }

    fun setupTransition(t : MutableList<TransitionData<E,Q>>){
        t.forEach {
            states[it.from]?.transitions?.set(it.symbolFromTape, Triple(it.to, it.symbolToWriteOnTape, it.headDirection))
        }
        if(t.isNotEmpty()) setupFlag = setupFlag or 128
    }

    fun addInput(i : List<E>) {
        resetMachine()
        tape = i.toMutableList()
        tape.add(emptySymbol)
    }

    fun resetMachine(){
        tape.clear()
        currentState = states[startState]
        head = 0
        machineStep = null
    }

    fun getVizFormat() : String{
        var vizStr : String = "digraph finite_state_machine { " +
                "fontname=\"Helvetica,Arial,sans-serif\" " +
                "node [fontname=\"Helvetica,Arial,sans-serif\"] " +
                "edge [fontname=\"Helvetica,Arial,sans-serif\"] " +
                "rankdir=LR; " +
                "node [shape = point ]; start; "

        vizStr += "node [shape = doublecircle]; ${acceptState.toString()}; "

        vizStr += "node [shape = circle]; "

        val transStr : HashMap<Pair<Q,Q>, MutableList<String>> = hashMapOf()

        states.forEach { state->
            state.value.transitions.forEach { transition ->
                val pair = Pair(state.key, transition.value.first)
                if(transStr[pair] == null) transStr[pair] = mutableListOf()
                transStr[pair]?.add("${transition.key} -> ${transition.value.second},${transition.value.third.data}")
            }
        }

        transStr.forEach {
            vizStr += "${it.key.first.toString()} -> ${it.key.second.toString()} [label = \"${it.value.customToStringNewLine()}\"]; "
        }

        vizStr += "start -> ${startState?.toString()}; "


        vizStr +="}"

        return vizStr
    }

    fun nextStep() : CurrentStateData<E,Q>?{
        if(currentState?.stateLabel == acceptState ||
            currentState?.stateLabel == rejectState ||
            head < 0 ||
                tape.isEmpty()){
            return null
        }

        val cur = currentState?.stateLabel
        val curHead = tape[head]
        val data = currentState?.transit(tape[head])

        val step = MachineStep<E,Q>()

        step.state = currentState
        step.head = head
        step.headValue = curHead

        if(machineStep == null)
            machineStep = step
        else{
            step.previousStep = machineStep
            machineStep = step
        }
        val wroteHeadPosition = head
        if(data == null){
            currentState = states[rejectState]
            return CurrentStateData(cur, curHead, currentState?.stateLabel, null, null, wroteHeadPosition)
        }
        tape[head] = data.second

        currentState = states[data.first]

        when(data.third){
            HeadDirection.ESQUERDA -> {
                head--
            }
            HeadDirection.DIREITA -> {
                head++
            }
            else -> {}
        }
        return CurrentStateData(cur, curHead, data.first, data.second, data.third, wroteHeadPosition)
    }

    data class PreviousData<E,Q>(
        var was : Q,
        var now : Q,
        var previousHeadValue : E,
        var previousHeadDirection : HeadDirection,
        val headPosition : Int)

    fun previousStep() : PreviousData<E,Q>?{
        if(machineStep != null) {
            val currentHead = head
            val previousHead = machineStep?.head!!
            val prev = currentState?.stateLabel!!
            val now = machineStep?.state?.stateLabel!!
            val previousHeadValue = machineStep?.headValue!!
            currentState = machineStep?.state
            head = machineStep?.head!!
            tape[head] = machineStep?.headValue
            machineStep = machineStep?.previousStep
            val headPreviousDirection = if(currentHead > previousHead) HeadDirection.ESQUERDA else HeadDirection.DIREITA
            return PreviousData(prev,now, previousHeadValue, headPreviousDirection, previousHead)
        }
        return null
    }

    fun hasMachineHalted() : Boolean{
        return currentState?.stateLabel == acceptState || currentState?.stateLabel == rejectState
    }

    private inner class MachineStep<E,Q>{
        var previousStep : MachineStep<E,Q>? = null

        var state : Node<E,Q>? = null
        var head = 0
        var headValue : E? = null
    }

    data class CurrentStateData<E,Q>(
        val whereItWas : Q?,
        val symbolRead : E?,
        val whereItsNow : Q?,
        val symbolWrote : E?,
        val headDirection: HeadDirection?,
        val headPosition : Int){
        override fun toString(): String {
            return "$whereItWas, $symbolRead --> $whereItsNow, $symbolWrote, $headDirection"
        }
    }

    class Node<E,Q>(var stateLabel : Q? = null){

        // read value -> where to go, value to write, direction
        var transitions : HashMap<E, Triple<Q, E, HeadDirection>> = HashMap()


        fun transit(symbol : E?) : Triple<Q, E, HeadDirection>? = if(symbol == null) null else transitions[symbol]
    }

    data class TransitionData <E,Q>(
        var from : Q,
        var symbolFromTape : E,
        var to : Q,
        var symbolToWriteOnTape : E,
        var headDirection: HeadDirection){

        override fun toString(): String {
            return "$from, $symbolFromTape --> $to, $symbolToWriteOnTape, $headDirection"
        }
    }


    enum class HeadDirection(val data : String){
        NONE(""),ESQUERDA("L"), DIREITA("R")
    }

    fun <T> MutableList<T>.customToStringNewLine() : String{
        var str : String = String()
        forEach {
            str += "${it.toString()}\n"
        }
        return str.dropLast(1)
    }


}

fun <T> MutableSet<T>.customToStringWithComma() : String{
    var str : String = String()
    forEach {
        str = str.plus("${it.toString()},")
    }
    return str.dropLast(1) ?: ""
}

fun <T,E> HashMap<T,E>.customToStringWithComma() : String{
    var str : String = String()
    forEach {
        str += "${it.key.toString()},"
    }
    return str.dropLast(1)
}


fun main(){

    val t = TuringMachine<String, String>()
    t.emptySymbol = "␣"

        //TM to accept strings  that ends with 101
//    t.addStates("q0,q1,q2,q3,qaccept,qreject".split(",").toList())
//    t.addInputAlphabet("0,1".split(",").toList())
//    t.addTapeAlphabet("0,1".split(",").toList())
//    t.setAcceptState("qaccept")
//    t.setRejectState("qreject")
//    t.setStartState("q0")
////
//    t.setupTransition(mutableListOf(
//        TuringMachine.TransitionData("q0", "0", "q0", "0", TuringMachine.HeadDirection.RIGHT),
//        TuringMachine.TransitionData("q0", "1", "q1", "1", TuringMachine.HeadDirection.RIGHT),
//
//        TuringMachine.TransitionData("q1", "1", "q1", "1", TuringMachine.HeadDirection.RIGHT),
//        TuringMachine.TransitionData("q1", "0", "q2", "0", TuringMachine.HeadDirection.RIGHT),
//
//        TuringMachine.TransitionData("q2", "0", "q0", "0", TuringMachine.HeadDirection.RIGHT),
//        TuringMachine.TransitionData("q2", "1", "q3", "1", TuringMachine.HeadDirection.RIGHT),
//
//        TuringMachine.TransitionData("q3", "0", "q2", "0", TuringMachine.HeadDirection.RIGHT),
//        TuringMachine.TransitionData("q3", t.emptySymbol!!, "qaccept", t.emptySymbol!!, TuringMachine.HeadDirection.LEFT)
//    ))
//    t.addInput(listOf("1","0","1","0","1"))
//
//
////    //TM to accept strings  all strings of the form 0^n1^n
////    t.addStates("q0,q1,q2,qaccept,qreject,q4".split(",").toList())
////    t.addInputAlphabet("0,1".split(",").toList())
////    t.addTapeAlphabet("0,1,x".split(",").toList())
////    t.setAcceptState("qaccept")
////    t.setRejectState("qreject")
////    t.setStartState("q0")
////
////    t.setupTransition(mutableListOf(
////        TuringMachine.TransitionData("q0", "0", "q1", t.emptySymbol, TuringMachine.HeadDirection.RIGHT),
////        TuringMachine.TransitionData("q0", t.emptySymbol, "qaccept", t.emptySymbol, TuringMachine.HeadDirection.LEFT),
////        TuringMachine.TransitionData("q0", "x", "q4", "x", TuringMachine.HeadDirection.RIGHT),
////
////        TuringMachine.TransitionData("q1", "x", "q1", "x", TuringMachine.HeadDirection.RIGHT),
////        TuringMachine.TransitionData("q1", "0", "q1", "0", TuringMachine.HeadDirection.RIGHT),
////        TuringMachine.TransitionData("q1", "1", "q2", "x", TuringMachine.HeadDirection.LEFT),
////
////        TuringMachine.TransitionData("q2", "0", "q2", "0", TuringMachine.HeadDirection.LEFT),
////        TuringMachine.TransitionData("q2", "x", "q2", "x", TuringMachine.HeadDirection.LEFT),
////        TuringMachine.TransitionData("q2", t.emptySymbol, "q0", t.emptySymbol, TuringMachine.HeadDirection.RIGHT),
////
////        TuringMachine.TransitionData("q4", "x", "q4", "x", TuringMachine.HeadDirection.RIGHT),
////        TuringMachine.TransitionData("q4", t.emptySymbol, "qaccept", t.emptySymbol, TuringMachine.HeadDirection.LEFT)
////    ))
//
//
    //TM to accept strings that contains 101
//    t.addStates("q0,q1,q2,qaccept,qreject".split(",").toList())
//    t.addInputAlphabet("0,1".split(",").toList())
//    t.addTapeAlphabet("0,1".split(",").toList())
//    t.setAcceptState("qaccept")
//    t.setRejectState("qreject")
//    t.setStartState("q0")
//
//    t.setupTransition(mutableListOf(
//        TuringMachine.TransitionData("q0", "0", "q0", "0", TuringMachine.HeadDirection.RIGHT),
//        TuringMachine.TransitionData("q0", "1", "q1", "1", TuringMachine.HeadDirection.RIGHT),
//
//        TuringMachine.TransitionData("q1", "0", "q2", "0", TuringMachine.HeadDirection.RIGHT),
//        TuringMachine.TransitionData("q1", "1", "q1", "1", TuringMachine.HeadDirection.RIGHT),
//
//        TuringMachine.TransitionData("q2", "0", "q0", "0", TuringMachine.HeadDirection.RIGHT),
//        TuringMachine.TransitionData("q2", "1", "qaccept", "1", TuringMachine.HeadDirection.RIGHT)
//    ))

    //t.addInput(listOf("1","0","1","0","1"))
////
//
////    val t = TuringMachine<String, String>(input, "␣")
////    //B = {w#w| w ∈ {0,1}∗}
    t.addStates("q1,q2,q3,q4,q5,q6,q7,q8,qaccept,qreject".split(",").toList())
    t.addInputAlphabet("0,1,#".split(",").toList())
    t.addTapeAlphabet("0,1,#,X".split(",").toList())
    t.acceptState = "qaccept"
    t.rejectState = "qreject"
    t.startState = "q1"

    t.setupTransition(mutableListOf(
        TuringMachine.TransitionData("q1", "0", "q2", "X", TuringMachine.HeadDirection.DIREITA),
        TuringMachine.TransitionData("q1", "#", "q8", "#", TuringMachine.HeadDirection.DIREITA),
        TuringMachine.TransitionData("q1", "1", "q3", "X", TuringMachine.HeadDirection.DIREITA),

        TuringMachine.TransitionData("q2", "0", "q2", "0", TuringMachine.HeadDirection.DIREITA),
        TuringMachine.TransitionData("q2", "1", "q2", "1", TuringMachine.HeadDirection.DIREITA),
        TuringMachine.TransitionData("q2", "#", "q4", "#", TuringMachine.HeadDirection.DIREITA),

        TuringMachine.TransitionData("q3", "0", "q3", "0", TuringMachine.HeadDirection.DIREITA),
        TuringMachine.TransitionData("q3", "1", "q3", "1", TuringMachine.HeadDirection.DIREITA),
        TuringMachine.TransitionData("q3", "#", "q5", "#", TuringMachine.HeadDirection.DIREITA),

        TuringMachine.TransitionData("q4", "X", "q4", "X", TuringMachine.HeadDirection.DIREITA),
        TuringMachine.TransitionData("q4", "0", "q6", "X", TuringMachine.HeadDirection.ESQUERDA),

        TuringMachine.TransitionData("q5", "X", "q5", "X", TuringMachine.HeadDirection.DIREITA),
        TuringMachine.TransitionData("q5", "1", "q6", "X", TuringMachine.HeadDirection.ESQUERDA),

        TuringMachine.TransitionData("q6", "0", "q6", "0", TuringMachine.HeadDirection.ESQUERDA),
        TuringMachine.TransitionData("q6", "1", "q6", "1", TuringMachine.HeadDirection.ESQUERDA),
        TuringMachine.TransitionData("q6", "X", "q6", "X", TuringMachine.HeadDirection.ESQUERDA),
        TuringMachine.TransitionData("q6", "#", "q7", "#", TuringMachine.HeadDirection.ESQUERDA),

        TuringMachine.TransitionData("q7", "0", "q7", "0", TuringMachine.HeadDirection.ESQUERDA),
        TuringMachine.TransitionData("q7", "1", "q7", "1", TuringMachine.HeadDirection.ESQUERDA),
        TuringMachine.TransitionData("q7", "X", "q1", "X", TuringMachine.HeadDirection.DIREITA),

        TuringMachine.TransitionData("q8", "X", "q8", "X", TuringMachine.HeadDirection.DIREITA),
        TuringMachine.TransitionData("q8", "␣", "qaccept", "␣", TuringMachine.HeadDirection.DIREITA)
    ))
    t.addInput(listOf("1","0","#","1","0"))
//
//    val str = t.getVizFormat()
//
//    val a = 9
    //graph [ranksep="0.6", nodesep="0.8"];

//
//    //Turing machine (TM) M2 that decides A = {0^2^n | n ≥ 0}
//    t.addStates("q1,q2,q3,q4,q5,qaccept,qreject")
//    t.addInputAlphabet("0")
//    t.addTapeAlphabet("0,X")
//    t.setAcceptState("qaccept")
//    t.setRejectState("qreject")
//    t.setStartState("q1")
//
//    t.setupTransition(mutableListOf(
//        TuringMachine.TransitionData("q1", "␣", "qreject", "␣", TuringMachine.HeadDirection.RIGHT),
//        TuringMachine.TransitionData("q1", "X", "qreject", "X", TuringMachine.HeadDirection.RIGHT),
//        TuringMachine.TransitionData("q1", "0", "q2", "␣", TuringMachine.HeadDirection.RIGHT),
//        TuringMachine.TransitionData("q2", "X", "q2", "X", TuringMachine.HeadDirection.RIGHT),
//        TuringMachine.TransitionData("q2", "␣", "qaccept", "␣", TuringMachine.HeadDirection.RIGHT),
//        TuringMachine.TransitionData("q2", "0", "q3", "X", TuringMachine.HeadDirection.RIGHT),
//        TuringMachine.TransitionData("q3", "X", "q3", "X", TuringMachine.HeadDirection.RIGHT),
//        TuringMachine.TransitionData("q3", "0", "q4", "0", TuringMachine.HeadDirection.RIGHT),
//        TuringMachine.TransitionData("q3", "␣", "q5", "␣", TuringMachine.HeadDirection.LEFT),
//        TuringMachine.TransitionData("q4", "X", "q4", "X", TuringMachine.HeadDirection.RIGHT),
//        TuringMachine.TransitionData("q4", "0", "q3", "X", TuringMachine.HeadDirection.RIGHT),
//        TuringMachine.TransitionData("q4", "␣", "qreject", "␣", TuringMachine.HeadDirection.RIGHT),
//        TuringMachine.TransitionData("q5", "0", "q5", "0", TuringMachine.HeadDirection.LEFT),
//        TuringMachine.TransitionData("q5", "X", "q5", "X", TuringMachine.HeadDirection.LEFT),
//        TuringMachine.TransitionData("q5", "␣", "q2", "␣", TuringMachine.HeadDirection.RIGHT)))
//
//
    while(true){
        val currentState = t.nextStep()
        println(currentState)
        if(currentState?.whereItsNow == "qaccept" || currentState?.whereItsNow == "qreject") break
    }

    while(true){
        val data = t.previousStep() ?: break
        println(data)
    }

    while(true){
        val currentState = t.nextStep()
        println(currentState)
        if(currentState?.whereItsNow == "qaccept" || currentState?.whereItsNow == "qreject") break
    }
}