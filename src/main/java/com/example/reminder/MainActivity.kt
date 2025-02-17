package com.example.reminder

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            isGranted: Boolean -> if(!isGranted) {
        Toast.makeText(this, R.string.permission_warning, Toast.LENGTH_LONG).show()
    }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, true)
        window.statusBarColor = Color.BLACK

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
            && !NotificationManagerCompat.from(this).areNotificationsEnabled()) {
            requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
            && !alarmManager.canScheduleExactAlarms()) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            startActivity(intent)
        }


        setContent {
            val systemUiController: SystemUiController = rememberSystemUiController()
            systemUiController.isSystemBarsVisible = false
            systemUiController.isNavigationBarVisible = false
            systemUiController.isSystemBarsVisible = false

            val viewModel: RemindersViewModel = viewModel()

            viewModel.dbHelper = DatabaseHelper(LocalContext.current)
            viewModel.alarmManager = alarmManager

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                colorResource(id = R.color.black),
                                colorResource(id = R.color.navy)
                            )
                        )
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                val medicines = listOf(
                    "Aspirin",
                    "Ibuprofen",
                    "Paracetamol",
                    "Amoxicillin",
                    "Metformin",
                    "Simvastatin",
                    "Lisinopril",
                    "Omeprazole",
                    "Levothyroxine",
                    "Gabapentin",
                    "Hydrochlorothiazide",
                    "Citalopram",
                    "Sertraline",
                    "Losartan",
                    "Atorvastatin",
                    "Fluoxetine",
                    "Furosemide",
                    "Montelukast",
                    "Albuterol",
                    "Prednisone",
                    "Clonazepam",
                    "Trazodone",
                    "Tramadol",
                    "Zolpidem",
                    "Dextromethorphan",
                    "Baclofen",
                    "Ranitidine",
                    "Atenolol",
                    "Rosuvastatin",
                    "Diazepam",
                    "Loratadine",
                    "Cetirizine",
                    "Naproxen",
                    "Nitroglycerin",
                    "Warfarin",
                    "Clopidogrel",
                    "Mirtazapine",
                    "Duloxetine",
                    "Venlafaxine",
                    "Metoprolol",
                    "Sildenafil",
                    "Bupropion",
                    "Acetaminophen",
                    "Chlorpheniramine",
                    "Guaifenesin",
                    "Cefalexin",
                    "Azithromycin",
                    "Ciprofloxacin",
                    "Tamsulosin",
                    "Lansoprazole",
                    "Amlodipine",
                    "Dapagliflozin"
                )
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "home") {
                    composable("home") { HomeScreen(navController, viewModel) }
                    composable("details") { DetailsScreen(navController, medicines) }
                }
            }
        }
    }
}

@Composable
fun AppTitle() {
    Text(
        text = stringResource(id = R.string.app_title),
        style = TextStyle(
            color = colorResource(id = R.color.white),
            fontSize = 26.sp,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Thin
        )
    )
}


@Composable
fun HomeScreen(navController: NavController, viewModel: RemindersViewModel) {
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        colorResource(id = R.color.black),
                        colorResource(id = R.color.navy)
                    )
                )
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        AppTitle()
        Form(viewModel)
        List(viewModel)
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = { navController.navigate("details") },
            interactionSource = interactionSource
        ) {
            Text("list of medications")
        }
    }
}

@Composable
fun DetailsScreen(navController: NavController, medicines: List<String>) {
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        colorResource(id = R.color.black),
                        colorResource(id = R.color.navy)
                    )
                )
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "list of medications",
            style = TextStyle(fontSize = 24.sp, color = colorResource(id = R.color.white))
        )


        LazyColumn(
            modifier = Modifier.height(800.dp)
        ) {
            items(medicines) { medicine ->
                Text(
                    text = medicine,
                    style = TextStyle(fontSize = 18.sp, color = colorResource(id = R.color.white)),
                    modifier = Modifier.padding(8.dp)
                )
            }
        }


        Button(
            onClick = { navController.popBackStack() },
            interactionSource = interactionSource,
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Back to Home")
        }
    }
}