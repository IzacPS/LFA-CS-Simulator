package app.fs.simulator.ui.compiler

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import app.fs.lfa.fsm.DFA
import app.fs.lfa.fsm.ENFA
import app.fs.lfa.fsm.NFA
import app.fs.lfa.tm.TuringMachine
import com.google.gson.Gson
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets

class MachineViewModel(application: Application) : AndroidViewModel(application) {
    private val _webData = MutableLiveData<String>().apply { value = "" }
    val webData : LiveData<String> = _webData
    var baseURL = "file:///android_asset/javascript/fsm_visual_engine/index_fsm.html"

    var tapeView : MutableList<String> = MutableList(200){
        " "
    }

    var playerButtonState = MutableLiveData<Boolean>().apply { value = false }

    var currentMachine = MutableLiveData<TuringMachine<String,String>>().apply {
        value = null
    }

    var currentDfa = MutableLiveData<DFA<String,String>>().apply {
        value = null
    }

    var currentNfa = MutableLiveData<NFA<String,String>>().apply {
        value = NFA()
    }

    var currentEnfa = MutableLiveData<ENFA<String,String>>().apply {
        value = ENFA("e")
    }


    fun getAutomataExamplesFromLocalFile(example: Int){
        try {
            val stream : InputStream
            val gson = Gson()
            when(example){
                1 -> {
                    stream = getApplication<Application>()
                        .resources
                        .assets
                        .open("javascript/fsm_visual_engine/examples/dfa/starts_1_ends_0.json")
                    val str = String(stream.readBytes(), StandardCharsets.UTF_8)
                    val dfa : DFA<String,String> = gson.fromJson(str, DFA<String,String>().javaClass)
                    dfa.setIsReady(true)
                    currentDfa.value = dfa
                    stream.close()
                }
                2 -> {
                    stream = getApplication<Application>()
                        .resources
                        .assets
                        .open("javascript/fsm_visual_engine/examples/dfa/even_number_of_zeros.json")
                    val str = String(stream.readBytes(), StandardCharsets.UTF_8)
                    val dfa : DFA<String,String> = gson.fromJson(str, DFA<String,String>().javaClass)
                    dfa.setIsReady(true)
                    currentDfa.value = dfa
                    stream.close()
                }
                3 -> {
                    stream = getApplication<Application>()
                        .resources
                        .assets
                        .open("javascript/fsm_visual_engine/examples/dfa/three_consecutive_zeros.json")
                    val str = String(stream.readBytes(), StandardCharsets.UTF_8)
                    val dfa : DFA<String,String> = gson.fromJson(str, DFA<String,String>().javaClass)
                    dfa.setIsReady(true)
                    currentDfa.value = dfa
                    stream.close()
                }
                4 -> {
                    stream = getApplication<Application>()
                        .resources
                        .assets
                        .open("javascript/fsm_visual_engine/examples/dfa/even_number_of_bs.json")
                    val str = String(stream.readBytes(), StandardCharsets.UTF_8)
                    val dfa : DFA<String,String> = gson.fromJson(str, DFA<String,String>().javaClass)
                    dfa.setIsReady(true)
                    currentDfa.value = dfa
                    stream.close()
                }
                5 -> {
                    stream = getApplication<Application>()
                        .resources
                        .assets
                        .open("javascript/fsm_visual_engine/examples/dfa/doesnt_have_substr_bb.json")
                    val str = String(stream.readBytes(), StandardCharsets.UTF_8)
                    val dfa : DFA<String,String> = gson.fromJson(str, DFA<String,String>().javaClass)
                    dfa.setIsReady(true)
                    currentDfa.value = dfa
                    stream.close()
                }
                6 -> {
                    stream = getApplication<Application>()
                        .resources
                        .assets
                        .open("javascript/fsm_visual_engine/examples/dfa/starts_with_aa_or_bb.json")
                    val str = String(stream.readBytes(), StandardCharsets.UTF_8)
                    val dfa : DFA<String,String> = gson.fromJson(str, DFA<String,String>().javaClass)
                    dfa.setIsReady(true)
                    currentDfa.value = dfa
                    stream.close()
                }
                7 -> {
                    stream = getApplication<Application>()
                        .resources
                        .assets
                        .open("javascript/fsm_visual_engine/examples/dfa/even_binary_numbers.json")
                    val str = String(stream.readBytes(), StandardCharsets.UTF_8)
                    val dfa : DFA<String,String> = gson.fromJson(str, DFA<String,String>().javaClass)
                    dfa.setIsReady(true)
                    currentDfa.value = dfa
                    stream.close()
                }
                8 -> {
                    stream = getApplication<Application>()
                        .resources
                        .assets
                        .open("javascript/fsm_visual_engine/examples/dfa/odd_binary_numbers.json")
                    val str = String(stream.readBytes(), StandardCharsets.UTF_8)
                    val dfa : DFA<String,String> = gson.fromJson(str, DFA<String,String>().javaClass)
                    dfa.setIsReady(true)
                    currentDfa.value = dfa
                    stream.close()
                }
            }
        }catch (e : IOException){
//TODO: check if this read of data bytes may fail.
        }
    }

    fun getTuringMachineExampleFromLocalFile(example : Int){
        try {
            val stream : InputStream
            val gson = Gson()
            when(example){
                1 -> {
                    stream = getApplication<Application>()
                        .resources
                        .assets
                        .open("javascript/fsm_visual_engine/examples/tm/ends_on_101.json")
                    val str = String(stream.readBytes(), StandardCharsets.UTF_8)
                    val m : TuringMachine<String,String> = gson.fromJson(str, TuringMachine<String,String>().javaClass).apply {
                        resetMachine()
                        setIsReady(true)
                    }
                    currentMachine.value = m
                    stream.close()
                }
                2 -> {
                    stream = getApplication<Application>()
                        .resources
                        .assets
                        .open("javascript/fsm_visual_engine/examples/tm/contains_101.json")
                    val str = String(stream.readBytes(), StandardCharsets.UTF_8)
                    val m : TuringMachine<String,String> = gson.fromJson(str, TuringMachine<String,String>().javaClass).apply {
                        resetMachine()
                        setIsReady(true)
                    }
                    currentMachine.value = m
                    stream.close()
                }
                3 -> {
                    stream = getApplication<Application>()
                        .resources
                        .assets
                        .open("javascript/fsm_visual_engine/examples/tm/accepts_w_hashtag_w_strings.json")
                    val str = String(stream.readBytes(), StandardCharsets.UTF_8)
                    val m : TuringMachine<String,String> = gson.fromJson(str, TuringMachine<String,String>().javaClass).apply {
                        resetMachine()
                        setIsReady(true)
                    }
                    currentMachine.value = m
                    stream.close()
                }
                4 -> {
                    stream = getApplication<Application>()
                        .resources
                        .assets
                        .open("javascript/fsm_visual_engine/examples/tm/accepts_0n_1n.json")
                    val str = String(stream.readBytes(), StandardCharsets.UTF_8)
                    val m : TuringMachine<String,String> = gson.fromJson(str, TuringMachine<String,String>().javaClass).apply {
                        resetMachine()
                        setIsReady(true)
                    }
                    currentMachine.value = m
                    stream.close()
                }
                5 -> {
                    stream = getApplication<Application>()
                        .resources
                        .assets
                        .open("javascript/fsm_visual_engine/examples/tm/accepts_0_tpof_2_tpof_n.json")
                    val str = String(stream.readBytes(), StandardCharsets.UTF_8)
                    val m : TuringMachine<String,String> = gson.fromJson(str, TuringMachine<String,String>().javaClass).apply {
                        resetMachine()
                        setIsReady(true)
                    }
                    currentMachine.value = m
                    stream.close()
                }
            }
        }catch (e : IOException){
//TODO: check if this read of data bytes may fail.
        }
    }

    init {

        try {
            val stream = getApplication<Application>()
                .resources
                .assets
                .open("javascript/fsm_visual_engine/index_fsm.html")

            _webData.value = String(stream.readBytes(), StandardCharsets.UTF_8)
        }catch (e : IOException){
//TODO: check if this read of data bytes may fail.
        }
    }

}