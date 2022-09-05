package id.smaraputra.storyapp.view.liststory

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import id.smaraputra.storyapp.R
import id.smaraputra.storyapp.data.local.entity.StoryModel
import id.smaraputra.storyapp.databinding.RvListStoryBinding
import id.smaraputra.storyapp.view.detailstory.DetailStoryActivity
import java.text.SimpleDateFormat
import java.util.*

class ListStoryAdapter(private val ctx: Context) : PagingDataAdapter<StoryModel, ListStoryAdapter.ListViewHolder>(DIFF_CALLBACK) {
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val itemBinding = RvListStoryBinding.inflate(LayoutInflater.from(ctx), parent, false)
        return ListViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val data = getItem(position)
        Glide.with(ctx)
            .asBitmap()
            .load(data?.photoUrl)
            .placeholder(R.drawable.ic_background_logo)
            .error(R.drawable.ic_background_logo)
            .into(holder.binding.userStoryPhoto)
        holder.binding.namaUser.text = if(!data?.name.isNullOrEmpty()) data?.name else holder.itemView.context.getString(R.string.no_data)
        holder.binding.waktuStory.text = if(!data?.createdAt.isNullOrEmpty()) data?.createdAt?.withDateFormat() else holder.itemView.context.getString(R.string.no_data)
        holder.binding.descriptionList.text = if(!data?.description.isNullOrEmpty()) data?.description else holder.itemView.context.getString(R.string.no_data)
        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(position)
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(holder.itemView.context, DetailStoryActivity::class.java)
                intent.putExtra(DETAIL_STORY, data)
                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        holder.itemView.context as Activity,
                        Pair(holder.binding.userStoryPhoto, "image"),
                        Pair(holder.binding.namaUser, "name"),
                        Pair(holder.binding.descriptionList, "description"),
                        Pair(holder.binding.waktuStory, "time"),
                    )
                holder.itemView.context.startActivity(intent, optionsCompat.toBundle())
            },100)
        }
    }

    class ListViewHolder(val binding: RvListStoryBinding) : RecyclerView.ViewHolder(binding.root)

    private fun String.withDateFormat(): String? {
        val mInputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val mOutputDateFormat = SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.getDefault())
        val mInputParsed: Date = mInputDateFormat.parse(this)!!
        return mOutputDateFormat.format(mInputParsed)
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: Int)
    }

    companion object {
        const val DETAIL_STORY = "detail_story"
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryModel>() {
            override fun areItemsTheSame(oldItem: StoryModel, newItem: StoryModel): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: StoryModel, newItem: StoryModel): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}
