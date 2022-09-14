package com.example.myapplication.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.R
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.viewmodel.LoginVIewModel
import dagger.hilt.android.AndroidEntryPoint
import io.getstream.chat.android.compose.ui.channels.ChannelsScreen
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {

    private val viewModel :LoginVIewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        subscribeToEvents()

        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
             LoginScreen()

            }
        }
    }

    @Composable
    fun LoginScreen ( ){
           //states :
        var userName by remember { mutableStateOf("")}
        var showProgressBar by remember { mutableStateOf(false)}

        viewModel.loadingState.observe(this, Observer { uiLoadingState ->
            showProgressBar = when(uiLoadingState){
                is LoginVIewModel.UiLoadingState.Loading ->{
                    true
                }
                is LoginVIewModel.UiLoadingState.NotLoading ->{
                    false
                }
            }

        })

        //he used a contraint layout with SerReferencece() i don't know what that is
        Column(
            modifier = Modifier.fillMaxSize().background(Color.Black),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround
        ) {
            
            Spacer(modifier = Modifier.height(120.dp))
            
            Image(painter = painterResource(id = R.drawable.chatlogo)
                , contentDescription = "",
            modifier = Modifier.clip(CircleShape)
                )
            TextField(value = userName,
                onValueChange = { newText ->
                    userName = newText
                },
                label = { Text(" User Name ") })

           Row(
               modifier = Modifier.fillMaxWidth(),
               verticalAlignment = Alignment.CenterVertically,
               horizontalArrangement = Arrangement.Center
           ) {
               Button(onClick = {
                   viewModel.loginUser(userName,getString(R.string.jwt_token))
               }


               ) {
                   Text(text = "Login As User")
               }
               Spacer(modifier = Modifier.width(8.dp))
               Button(onClick = {
                   viewModel.loginUser(userName)
               }
               ) {
                   Text(text = "Login As Guest")
               }
           }

            if(showProgressBar){
                CircularProgressIndicator()
            }

        }
    }
  private fun subscribeToEvents(){

      lifecycleScope.launchWhenStarted {
          viewModel.loginEvent.collect{ event ->
              when(event){

              is LoginVIewModel.LoginEvents.ErrorInputTooShort -> {
                  showToast("Invalide! Entre more then 3 characters")
          }
              is LoginVIewModel.LoginEvents.ErrorLogIn -> {
                  val errorMessage = event.error
                  showToast("Error : $errorMessage")
              }
              is LoginVIewModel.LoginEvents.Success -> {
                  showToast("Login Successful")
                  startActivity(Intent(this@LoginActivity,ChannelListActivity::class.java))

                  //to destroy this activity
                  finish()
              }
              }

          }
      }
  }

    private fun showToast(msg:String){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show()
    }
}


