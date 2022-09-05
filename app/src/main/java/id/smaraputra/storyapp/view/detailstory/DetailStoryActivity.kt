package id.smaraputra.storyapp.view.detailstory

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import id.smaraputra.storyapp.R
import id.smaraputra.storyapp.data.local.entity.StoryModel
import id.smaraputra.storyapp.databinding.ActivityDetailStoryBinding
import java.text.SimpleDateFormat
import java.util.*

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        title = getString(R.string.detail_story_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val dataDetail = intent.getParcelableExtra<StoryModel>(DETAIL_STORY) as StoryModel

        binding.name.text = dataDetail.name
        binding.description.text = dataDetail.description
        binding.time.text = dataDetail.createdAt.withDateFormat()
        val location = "Lat : ${dataDetail.lat} | Lon : ${dataDetail.lon}"
        binding.location.text = location
        Glide.with(this)
            .asBitmap()
            .load(dataDetail.photoUrl)
            .placeholder(R.drawable.ic_background_logo)
            .error(R.drawable.ic_background_logo)
            .into(binding.imageView4)
    }

    private fun String.withDateFormat(): String {
        val mInputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val mOutputDateFormat = SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.getDefault())
        val mInputParsed: Date = mInputDateFormat.parse(this)!!
        return mOutputDateFormat.format(mInputParsed)
    }

    companion object {
        const val DETAIL_STORY = "detail_story"
    }
}