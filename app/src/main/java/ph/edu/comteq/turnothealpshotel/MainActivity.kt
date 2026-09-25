package ph.edu.comteq.turnothealpshotel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Abc
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.gson.Gson
import ph.edu.comteq.turnothealpshotel.Hotel
import androidx.compose.ui.layout.ContentScale


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TurnoTheAlpsHotelTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Homepage(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Homepage(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var hotels by remember {mutableStateOf(emptyList<Hotel>())}

    // Add a state variable to hold the search text
    var searchText by remember { mutableStateOf("") }

    // Load the hotels from the JSON file in the assets folder using Gson
    LaunchedEffect(Unit) {
        val json = context.assets.open("hotels.json")
            .bufferedReader()
            .use { it.readText() }
        val gson = Gson()
        val hotelsArray = gson.fromJson(
            json, Array<Hotel>::class.java)
        hotels = hotelsArray.toList()
    }
    // Use a Column to arrange the header, search bar, and hotel list vertically
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(8.dp)
    ) {
        // Add a header with the hotel name and the France flag
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                ),
                text = "The Alps Hotel",
                modifier = Modifier.padding(vertical = 16.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.france_national_flag),
                contentDescription = "France Flag",
                modifier = Modifier.size(35.dp)
            )
            // Add a spacer to push the user icon to the right
            Spacer(modifier = Modifier.weight(1f))

            // Display the user icon using an Icon composable
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "User Icon",
                modifier = Modifier.size(35.dp),
                tint = Color.Black
            )

            // icon from the right using image
//            Image(
//                painter = painterResource(id = R.drawable.outline_artist),
//                contentDescription = "User Icon",
//                modifier = Modifier.size(35.dp)
//            )
        }
        // Add a search bar to filter hotels by name
        Search(
            text = searchText,
            onValueChange = { searchText = it }
        )
        LazyColumn(
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Filter the hotels based on the search text
            val filteredHotels = if (searchText.isEmpty()) {
                hotels
            } else {
                hotels.filter { it.hotel_name.contains(searchText, ignoreCase = true) }
            }

            // Display the filtered hotels in the LazyColumn
            items(filteredHotels) { hotel ->
                HotelCard(hotel = hotel)
            }
        }
    }
}

@Composable
fun HotelCard(
    hotel: Hotel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        // Display the hotel cover image using Coil's AsyncImage
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data("file:///android_asset/${hotel.hotel_cover_image}")
                .crossfade(true)
                .build(),
            contentDescription = hotel.hotel_name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        // Add a small space between the image and the hotel name
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = hotel.hotel_name,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            fontSize = MaterialTheme.typography.titleMedium.fontSize
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Display the star icon for the hotel rating
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Rating",
                modifier = Modifier.size(16.dp),
                tint = Color(0xFFFFD700) // Gold color for the star
            )
            // Add a small space between the star icon and the rating text
            Spacer(modifier = Modifier.width(4.dp))

            // Display the hotel rating and distance to ski in a single Text composable
            Text(
                text = "${hotel.hotel_rating} | ${hotel.hotel_to_ski_distance} km to ski",
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                color = Color.Black
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomepagePreview() {
    TurnoTheAlpsHotelTheme {
        Homepage()
    }
}

@Composable
fun Search(
    text: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = text,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults
            .colors(
                focusedBorderColor = Color.Gray,
                unfocusedBorderColor = Color.LightGray,
                focusedLabelColor = Color.Gray,
                unfocusedLabelColor = Color.LightGray,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            ),
        placeholder = { Text(text = "Search a hotel name") }
    )
}