package ph.edu.comteq.turnothealpshotel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeCap.Companion.Square
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import ph.edu.comteq.turnothealpshotel.ui.theme.TurnoTheAlpsHotelTheme
import androidx.compose.ui.unit.dp
import com.google.gson.Gson

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TurnoTheAlpsHotelTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Homepage(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Homepage(name: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var hotels by remember {mutableStateOf(emptyList<Hotel>())}

    LaunchedEffect(Unit) {
        val json = context.assets.open("hotels.json")
            .bufferedReader()
            .use { it.readText() }
        val gson = Gson()
        val hotelsArray = gson.fromJson(json, Array<Hotel>::class.java)
        hotels = hotelsArray.toList()
    }
    Row(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text(
            text = "The Alps Hotel",
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )
        Image(
            painter = painterResource(id = R.drawable.france_national_flag),
            contentDescription = "France Flag",
            modifier = modifier.size(25.dp)
        )
        Image(
            painter = painterResource(id = R.drawable.outline_artist),
            contentDescription = "User Icon",
            modifier = modifier.size(25.dp)
        )

        var text by remember { mutableStateOf("") }
        OutlinedTextField(
            value = text,
            onValueChange = { newText ->
                text = newText
            },
            placeholder = {
                Text(
                    "Search a Hotel Name"
                )
            }
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(10.dp)
        ) {
            items(hotels) { hotel ->
                Text(hotel.hotel_name)
            }
        }
    }
}

private fun Modifier.clip(shape: StrokeCap) {}

@Preview(showBackground = true)
@Composable
fun HomepagePreview() {
    TurnoTheAlpsHotelTheme {
        Homepage("Android")
    }
}

