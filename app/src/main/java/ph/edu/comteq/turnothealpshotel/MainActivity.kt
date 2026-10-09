package ph.edu.comteq.turnothealpshotel

import java.io.FileNotFoundException
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import ph.edu.comteq.turnothealpshotel.ui.theme.TurnoTheAlpsHotelTheme
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.gson.Gson
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            TurnoTheAlpsHotelTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "homepage",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("homepage") {
                            Homepage(navController = navController)
                        }
                        composable("hotel_details/{hotelId}") { backStackEntry ->
                            val hotelId = backStackEntry.arguments
                                ?.getString("hotelId")
                                ?.toIntOrNull()
                            if (hotelId != null) {
                                HotelDetailsScreen(
                                    hotelId = hotelId,
                                    navController = navController
                                )
                            } else {
                                Text("Invalid hotel ID")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Homepage(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var hotels by remember { mutableStateOf(emptyList<Hotel>()) }
    var searchText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val json = context.assets.open("hotels.json")
            .bufferedReader()
            .use { it.readText() }
        val gson = Gson()
        val hotelsArray = gson.fromJson(json, Array<Hotel>::class.java)
        hotels = hotelsArray.toList()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(8.dp)
    ) {
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
            Spacer(modifier = Modifier.weight(1f))

            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "User Icon",
                modifier = Modifier.size(35.dp),
                tint = Color.Black
            )
        }

        Search(
            text = searchText,
            onValueChange = { searchText = it }
        )

        LazyColumn(
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val filteredHotels = if (searchText.isEmpty()) {
                hotels
            } else {
                hotels.filter { it.hotel_name.contains(searchText, ignoreCase = true) }
            }

            items(filteredHotels) { hotel ->
                HotelCard(
                    hotel = hotel,
                    onHotelClick = { selectedHotel ->
                        navController.navigate("hotel_details/${selectedHotel.hotel_id}")
                    }
                )
            }
        }
    }
}


@Composable
fun HotelCard(
    hotel: Hotel,
    modifier: Modifier = Modifier,
    onHotelClick: (Hotel) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onHotelClick(hotel) }
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelDetails(
    hotel: HotelDetail,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    selectedRoomIndex: Int?,
    onRoomSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        CompositionLocalProvider(LocalContentColor provides Color.Black) {
            Text(
                text = hotel.hotel_name,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(16.dp)
            )

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { onTabSelected(0) },
                    selectedContentColor = Color.Black,
                    unselectedContentColor = Color.Black,
                    text = { Text("Guest reviews") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { onTabSelected(1) },
                    selectedContentColor = Color.Black,
                    unselectedContentColor = Color.Black,
                    text = { Text("Room selection") }
                )
            }

            when (selectedTab) {
                0 -> GuestReviewsContent(
                    reviews = hotel.guest_reviews,
                    modifier = Modifier.weight(1f)
                )
                1 -> RoomSelectionContent(
                    rooms = hotel.rooms,
                    selectedRoomIndex = selectedRoomIndex,
                    onRoomSelected = onRoomSelected,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun GuestReviewsContent(
    reviews: GuestReviews,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Ratings",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        reviews.ratings_categories.forEach { category ->
            category.forEach { (name, rating) ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = name)
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = rating.toString(),
                            textAlign = TextAlign.End
                        )
                    }
                    LinearProgressIndicator(
                        progress = { (rating / 10.0).toFloat().coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = Color.LightGray
                    )
                }
            }
        }
        Text(
            text = "Reviews",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(top = 20.dp, bottom = 8.dp)
        )
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(end = 8.dp)
        ) {
            items(reviews.reviews_objects) { review ->
                GuestReview(
                    review = review,
                    modifier = Modifier
                        .width(280.dp)
                        .height(230.dp)
                )
            }
        }
    }
}

@Composable
private fun RoomSelectionContent(
    rooms: List<HotelRoom>,
    selectedRoomIndex: Int?,
    onRoomSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Rooms",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .padding(bottom = 8.dp)
        )
        rooms.forEachIndexed { index, room ->
            val isSelected = selectedRoomIndex == index
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(
                        width = 1.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable { onRoomSelected(index) }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = room.room_type,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text("Beds: ${room.room_bed_type}")
                            Text("Guests: ${room.room_total_number_of_guests}")
                        }
                        Text(
                            text = "€${room.room_price_for_one_night}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.End,
                            modifier = Modifier.padding(start = 12.dp)
                        )
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                            .border(1.dp, Color.LightGray, RoundedCornerShape(6.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "Features",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold

                        )
                        room.room_features.forEach { feature ->
                            Text(
                                text = feature,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelDetailsScreen(
    hotelId: Int,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
            .background(Color.White),
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = { Text(text = "Booking") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black,
                    actionIconContentColor = Color.Black
                ),

                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        val context = LocalContext.current
        var hotel by remember(hotelId) { mutableStateOf<HotelDetail?>(null) }
        var detailsUnavailable by remember(hotelId) { mutableStateOf(false) }
        var selectedTab by remember(hotelId) { mutableStateOf(0) }
        var selectedRoomIndex by remember(hotelId) { mutableStateOf<Int?>(null) }

        LaunchedEffect(hotelId) {
            val filename = "hotels_details.$hotelId.json"
            try {
                val json = context.assets.open(filename)
                    .bufferedReader()
                    .use { it.readText() }
                hotel = Gson().fromJson(json, HotelDetail::class.java)
            } catch (_: FileNotFoundException) {
                detailsUnavailable = true
            }
        }

        val loadedHotel = hotel
        when {
            loadedHotel != null -> HotelDetails(
                hotel = loadedHotel,
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                selectedRoomIndex = selectedRoomIndex,
                onRoomSelected = { selectedRoomIndex = it },
                modifier = Modifier.padding(innerPadding)
            )
            detailsUnavailable -> Text(
                text = "Details are not available for this hotel yet.",
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp),
                color = Color.Black
            )
            else -> Text(
                text = "Loading...",
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
            )
        }
    }
}

@Composable
fun GuestReview(
    review: GuestReview,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 8.dp)
            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Text(
            text = "${review.username} \n${review.country}",
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = review.review_text,
            color = Color.Black
        )
    }
}


// booking page
//@Composable
//fun BookingScreen(
//    hotel: Hotel,
//    modifier: Modifier = Modifier
//) {
//    Scaffold(
//        modifier = modifier.fillMaxSize(),
//        topBar = {
//            TopAppBar(
//                title = { Text(text = "Booking") }
//
//                )
//            )
//        }
//    ) { innerPadding ->
//        Column(
//            modifier = Modifier
//                .padding(innerPadding)
//                .padding(16.dp)
//        ) {
//            Text(text = "Hotel Name: ${hotel.hotel_name}")
//            Text(text = "Rating: ${hotel.hotel_rating}")
//            Text(text = "Distance to Ski: ${hotel.hotel_to_ski_distance} km")
//            // Add more booking details and UI elements as needed
//        }
//    })
//}


@Preview(showBackground = true)
@Composable
fun HomepagePreview() {
    TurnoTheAlpsHotelTheme {
        Homepage(navController = rememberNavController())
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