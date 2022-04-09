package app.fs.simulator

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import app.fs.simulator.databinding.ActivityMainBinding
import app.fs.simulator.interfaces.IButtonPressed
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

//        binding.appBarMain.fab.setOnClickListener { view ->
//            MachineDefinitionFragment.newInstance().show(supportFragmentManager, "MachineDefinitionFragment")
//        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        navController = findNavController(R.id.nav_host_fragment_content_main)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        val it = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main)


        it?.let {
            if(it.childFragmentManager.primaryNavigationFragment is IButtonPressed){
                (it.childFragmentManager.primaryNavigationFragment as IButtonPressed).onBackPressed()
            }else{
                super.onBackPressed()
            }
        }

//        navController.currentDestination?.let {
//            val frag = supportFragmentManager.findFragmentById(it.id)
//            (frag as? IButtonPressed)?.onBackPressed()?.not()?.let {
//                super.onBackPressed()
//            }
//        }

    }
}


//fun main(){
//    val gson = Gson()
//
//    val input : MutableList<String?> = mutableListOf("1","1","0","0","0","0","1","1","0","1","1","1")
//
//    //TM to accept strings  that ends with 101
//    val t = TuringMachine<String, String>("␣")
//
//    t.addStates("q1,q2,q3,q4,q5,qaccept,qreject".split(","))
//    t.addInputAlphabet(listOf("0"))
//    t.addTapeAlphabet("0,X".split(","))
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
//    val str = gson.toJson(t)
//
//    val hello = ""
//}