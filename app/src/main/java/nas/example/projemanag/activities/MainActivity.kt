package nas.example.projemanag.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import nas.example.projemanag.R
import nas.example.projemanag.adapters.BoardItemsAdapter
import nas.example.projemanag.firebase.FirestoreClass
import nas.example.projemanag.models.Board
import nas.example.projemanag.models.User
import nas.example.projemanag.utils.Constants

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object {
        const val MY_PROFILE_REQUEST_CODE : Int = 11
        const val CREATE_BOARD_REQUEST_CODE: Int = 12
    }

    private lateinit var mUserName : String
    private lateinit var mSharedPreferences: SharedPreferences

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var navUserImage: ImageView
    private lateinit var tvUsername: TextView
    private lateinit var fabCreateBoard: FloatingActionButton
    private lateinit var rvBoardsList: RecyclerView
    private lateinit var tvNoBoardsAvailable: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)

        val headerView = navigationView.getHeaderView(0)
        navUserImage = headerView.findViewById(R.id.nav_user_image)
        tvUsername = headerView.findViewById(R.id.tv_username)
        fabCreateBoard = findViewById(R.id.fab_create_board)
        rvBoardsList = findViewById(R.id.rv_boards_list)
        tvNoBoardsAvailable = findViewById(R.id.tv_no_boards_available)
        
        setupActionBar()
        navigationView.setNavigationItemSelectedListener(this)

        mSharedPreferences = this.getSharedPreferences(
            Constants.PROJEMANAG_PREFERENCES, Context.MODE_PRIVATE)
        

        //TODO ERASE
        val firestoreClass = FirestoreClass()
        firestoreClass.loadUserData(this, true)

        fabCreateBoard.setOnClickListener {
            val intent = Intent(this, CreateBoardActivity::class.java)
            intent.putExtra(Constants.NAME, mUserName)
            startActivityForResult(intent, CREATE_BOARD_REQUEST_CODE)
        }

        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        if (!sharedPreferences.getBoolean("has_seen_tutorial_${FirebaseAuth.getInstance().currentUser?.uid}", false)) {
            android.os.Handler().postDelayed({
                showTutorialOverlay()
            }, 500) // delay for 500ms
        }
    }


    fun populateBoardsListToUI(boardsList: ArrayList<Board>){
        hideProgressDialog()
        if(boardsList.size>0){
            rvBoardsList.visibility = View.VISIBLE
            tvNoBoardsAvailable.visibility =View.GONE

            rvBoardsList.layoutManager = LinearLayoutManager(this)
            rvBoardsList.setHasFixedSize(true)

            val adapter = BoardItemsAdapter(this,boardsList)
            rvBoardsList.adapter = adapter

            adapter.setOnClickListener(object :
                BoardItemsAdapter.OnClickListener {
                override fun onClick(position: Int, model: Board) {
                    val intent= Intent(this@MainActivity,TaskListActivity::class.java)
                    intent.putExtra(Constants.DOCUMENT_ID,model.documentId)
                    startActivity(intent)
                }
            })

        }else{
            rvBoardsList.visibility = View.GONE
            tvNoBoardsAvailable.visibility =View.VISIBLE
        }
    }


    private fun setupActionBar(){
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_main_activity)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_action_navigation_menu)

        toolbar.setNavigationOnClickListener{
            toggleDrawer()

        }
    }
    private fun toggleDrawer(){
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun updateNavigationUserDetails(user: User, readBoardsList: Boolean){
        hideProgressDialog()
        mUserName= user.name

        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(navUserImage)

        tvUsername.text = user.name

        if(readBoardsList){
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().getBoardsList(this)
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MY_PROFILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            FirestoreClass().loadUserData(this)
        }else if(requestCode == CREATE_BOARD_REQUEST_CODE
            && resultCode == Activity.RESULT_OK){
            FirestoreClass().getBoardsList(this)
        } else{
            Log.e("Cancelled", "Cancelled")
        }
    }



    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        Log.d("Navigation", "Item selected: ${item.itemId}")
        when(item.itemId){
            R.id.nav_my_profile -> {
                startActivityForResult(Intent(this, MyProfileActivity::class.java), MY_PROFILE_REQUEST_CODE)
            }
            R.id.nav_user_manual -> {
                Log.d("Navigation", "Starting UserManualActivity")
                startActivity(Intent(this, UserManualActivity::class.java))
            }
            R.id.nav_sign_out ->{
                val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
                FirebaseAuth.getInstance().signOut()
                val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
                if (currentUserId != null) {
                    sharedPreferences.edit().remove("has_seen_tutorial_$currentUserId").apply()
                }
                mSharedPreferences.edit().clear().apply()
                val intent = Intent(this, IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }


    fun tokenUpdateSuccess(){
        hideProgressDialog()
        val editor: SharedPreferences.Editor = mSharedPreferences.edit()
        editor.putBoolean(Constants.FCM_TOKEN_UPDATED, true)
        editor.apply()
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().loadUserData(this, true)
    }

    private fun updateFCMToken(token: String) {
        val userHashMap = HashMap<String, Any>()
        userHashMap[Constants.FCM_TOKEN] = token
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().updateUserProfileData(this, userHashMap)
    }

    fun showTutorialOverlay() {
        val overlay = layoutInflater.inflate(R.layout.tutorial_overlay, null)
        val decorView = window.decorView as ViewGroup
        decorView.addView(overlay)

        // Set click listener to dismiss the overlay
        overlay.setOnClickListener {
            decorView.removeView(overlay)
            // Save a flag to indicate that the user has seen the tutorial
            val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
            sharedPreferences.edit().putBoolean("has_seen_tutorial_${FirebaseAuth.getInstance().currentUser?.uid}", true).apply()
        }
    }

    //TODO ERASEEEE

}