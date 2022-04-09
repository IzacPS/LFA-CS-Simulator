package app.fs.simulator.ui.automata

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.io.IOException
import java.nio.charset.StandardCharsets

class AutomataViewModel(application: Application) : AndroidViewModel(application) {

    private val _webData = MutableLiveData<String>().apply { value = "" }
    val webData : LiveData<String> = _webData

    var baseURL = "file:///android_asset/javascript/fsm_visual_engine/index_fsm.html"

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

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