package app.fs.simulator.data

data class TransitionData(val from : String, val symbol : String, val to : String)

data class TransitionsFunctionData(val from : String, val symbol : String, val to : String){
    override fun toString() = "De $from lê $symbol e vai para $to"
}
