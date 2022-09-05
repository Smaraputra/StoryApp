package id.smaraputra.storyapp.view.widget

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.os.bundleOf
import androidx.room.Room
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import id.smaraputra.storyapp.R
import id.smaraputra.storyapp.data.local.entity.StoryModel
import id.smaraputra.storyapp.data.local.room.StoryDao
import id.smaraputra.storyapp.data.local.room.StoryRoomDatabase
import java.util.concurrent.ExecutionException


internal class StackRemoteViewsFactory(private val mContext: Context) : RemoteViewsService.RemoteViewsFactory {
    private val storyRoomDatabase: StoryRoomDatabase = Room.databaseBuilder(mContext, StoryRoomDatabase::class.java, "story_database").build()
    private val storyDao: StoryDao = storyRoomDatabase.storyDao()
    private lateinit var storyModel: List<StoryModel>
    private val mWidgetItems = ArrayList<String>()

    override fun onCreate() {
        storyModel = emptyList()
    }

    override fun onDataSetChanged() {
        storyModel = emptyList()
        storyModel = storyDao.getAllStoryDB()
        for(a in storyModel){
            a.photoUrl.let { mWidgetItems.add(it) }
        }
    }

    override fun onDestroy() {

    }

    override fun getCount(): Int = mWidgetItems.size

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(mContext.packageName, R.layout.widget_item)
        val futureTarget: FutureTarget<Bitmap> = Glide.with(mContext)
            .asBitmap()
            .load(mWidgetItems[position])
            .submit(250, 250)
        try {
            rv.setImageViewBitmap(R.id.imageView, futureTarget.get())
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        }

        val extras = bundleOf(
            ImagesBannerWidget.EXTRA_ITEM to position
        )
        val fillInIntent = Intent()
        fillInIntent.putExtras(extras)

        rv.setOnClickFillInIntent(R.id.imageView, fillInIntent)
        return rv
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(i: Int): Long = 0

    override fun hasStableIds(): Boolean = false

}