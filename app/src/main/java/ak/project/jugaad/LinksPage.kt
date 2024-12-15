package ak.project.jugaad
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

//import ak.project.jugaad.ui.theme.

class LinksPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
                LinksPageContent()
        }
    }
}

@Composable
fun LinksPageContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // SMVDU Logo at the top
        Image(
            painter = painterResource(id = R.drawable.smvdu_logo), // Replace with your logo resource ID
            contentDescription = "SMVDU Logo",
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 16.dp)
        )

        // University Introduction
        Text(
            text = stringResource(id = R.string.university_intro),
            style = MaterialTheme.typography.bodyLarge.copy(
                lineHeight = 24.sp,
                fontSize = 16.sp
            ),
            modifier = Modifier
                .padding(bottom = 24.dp)
                .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(8.dp))
                .padding(16.dp)
        )

        // Section: University Information
        SectionWithLinksEnhanced(
            title = stringResource(id = R.string.section_university_info),
            description = stringResource(id = R.string.university_info_desc),
            links = listOf(
                Pair(stringResource(id = R.string.link_about_us), "https://smvdu.ac.in/about-us/"),
                Pair(stringResource(id = R.string.location_label), " https://goo.gl/maps/Z4tU8J7k9gU8n4b19"),

                Pair(stringResource(id = R.string.link_contact_us), "https://smvdu.ac.in/contact-us/")
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Section: Academics
        SectionWithLinksEnhanced(
            title = stringResource(id = R.string.section_academics),
            description = stringResource(id = R.string.academics_desc),
            links = listOf(
                Pair(stringResource(id = R.string.link_notifications), "https://smvdu.ac.in/notifications/"),
                Pair(stringResource(id = R.string.link_results), "https://smvdu.ac.in/results/")
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Section: Placements
        SectionWithLinksEnhanced(
            title = stringResource(id = R.string.section_placements),
            description = stringResource(id = R.string.placements_desc),
            links = listOf(
                Pair(stringResource(id = R.string.link_placements), "https://smvdu.ac.in/placements/")
            )
        )
    }
}

@Composable
fun SectionWithLinksEnhanced(
    title: String,
    description: String,
    links: List<Pair<String, String>>
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        links.forEach { (linkName, linkUrl) ->
            Text(
                text = linkName,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.Blue
                ),
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .clickable { openUrl(context,linkUrl) }
            )
        }
    }
}


// Non-composable function to handle opening URLs
fun openUrl(context: android.content.Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    context.startActivity(intent)
}



