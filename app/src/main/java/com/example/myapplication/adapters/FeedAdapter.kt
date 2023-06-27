package com.example.myapplication.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.VideoView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.fragments.FeedFragment
import com.example.myapplication.interfaces.ClickInterface
import com.example.myapplication.interfaces.FeedLikeClickInterface
import com.example.myapplication.model.postmodel.Results
import com.example.myapplication.prefrences.Constants
import com.example.myapplication.prefrences.SharedPref
import com.example.myapplication.utils.Utils
import com.google.android.exoplayer2.ui.PlayerView
import kotlinx.android.synthetic.main.activity_teaser.*
import kotlinx.android.synthetic.main.cell_feed_list.view.*
import kotlinx.android.synthetic.main.cell_feed_list.view.count_textview
import java.util.*

class FeedAdapter(val items: List<Results>, val context: Context, val fragment: FeedFragment, var  clickListener: RecyclerViewClickListener) :
    RecyclerView.Adapter<FeedViewHolder>() {
    private var clickPosition: Int = -1
    private var oldPosition: Int = -1
    private var mClickInterface: FeedLikeClickInterface? = null
    private var mPostDetails: ClickInterface? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        return FeedViewHolder(LayoutInflater.from(context).inflate(R.layout.cell_feed_list, parent, false))
    }
    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
            try {
                this.mClickInterface = fragment
                this.mPostDetails = fragment
                if (SharedPref.read(SharedPref.PLAY_SONG_POSITION, -1) == position) {
                    clickPosition = SharedPref.read(SharedPref.PLAY_SONG_POSITION, -1)
                    SharedPref.clearKey(SharedPref.PLAY_SONG_POSITION)
                }
                Glide.with(context).load("http://44.231.47.188"+items[position].imagePath).error(R.drawable.app_image).error(R.drawable.app_image).into(holder.mPostImageView)
                holder.playIcon.setOnClickListener {
                    Constants.isShuffle=false
                    if (oldPosition != -1 && oldPosition == position) {
                        oldPosition = -1
                        clickPosition = -1
                        Constants.repeatCount=-1
                        clickListener.repeatClick(position, holder.mPlayerViewView, "http://44.231.47.188" + items[position].content.filePath,holder.playIcon)
                        holder.playIcon.setBackgroundResource(R.drawable.ic_play_circle_outline_black_24dp)
                        Utils.getInstance().killMediaPlayer(null)
                        clickListener.stopMediaPlayer(position)
                        notifyDataSetChanged()
                    } else {
                        Constants.isShuffle=false
                        if(items[position].content!=null&&items[position].content.filePath!=null) {
                            clickPosition = position
                            notifyDataSetChanged()
                            clickListener.onClick(position, holder.mPlayerViewView, "http://44.231.47.188" + items[position].content.filePath,holder.playIcon)
                        }
                    }
//                    clickListener.onClick(position, holder.mPlayerViewView, "http://44.231.47.188" + items[position].content.filePath,holder.playIcon)
                }
                holder.nextSongPlayIcon.setOnClickListener {
                    try {
                        if (position<items.size-1&&items[position + 1].content!=null&&items[position + 1].content.filePath!=null) {
                            clickPosition = position+1
                            notifyDataSetChanged()
                            clickListener.nextButtonClick(position + 1, holder.mPlayerViewView, "http://44.231.47.188" + items[position + 1].content.filePath,holder.playIcon)
                        }
                    } catch (e: Exception) {
                    }
                }

                holder.previousSongPlayIcon.setOnClickListener {
                    if(position>0&&position<items.size&&items[position - 1].content!=null&&items[position - 1].content.filePath!=null){
                        clickPosition = position-1
                        notifyDataSetChanged()
                    clickListener.previousButtonClick(position-1, holder.mPlayerViewView,"http://44.231.47.188"+items[position-1].content.filePath,holder.playIcon)
                }
                }
                holder.replaySongPlayIcon.setOnClickListener {
                    if(Constants.repeatCount<=1){
                        Constants.repeatCount=Constants.repeatCount+1
                        holder.mCountTextView.visibility=View.VISIBLE
                        holder.mCountTextView.text=Constants.repeatCount.toString()
                        holder.replaySongPlayIcon.setColorFilter(ContextCompat.getColor(context, R.color.purple), android.graphics.PorterDuff.Mode.MULTIPLY)
                    }else{
                        Constants.repeatCount=-1
                        holder.mCountTextView.visibility=View.GONE
                        holder.replaySongPlayIcon.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY)
                    }
                        clickListener.repeatClick(position, holder.mPlayerViewView, "http://44.231.47.188" + items[position].content.filePath,holder.playIcon)
                        notifyDataSetChanged()
                }
                if(Constants.repeatCount>=0){
                    holder.mCountTextView.visibility=View.VISIBLE
                    holder.mCountTextView.text = Constants.repeatCount.toString()
                    holder.replaySongPlayIcon.setColorFilter(ContextCompat.getColor(context, R.color.purple), android.graphics.PorterDuff.Mode.MULTIPLY)
                    if(Constants.repeatCount==2){
                        holder.mCountTextView.text="All"
                    }else{
                        holder.mCountTextView.text=Constants.repeatCount.toString()
                    }
                }else{
                    holder.mCountTextView.visibility=View.GONE
                    holder.mCountTextView.text = Constants.repeatCount.toString()
                    holder.replaySongPlayIcon.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY)

                }
                holder.randomSongPlayIcon.setOnClickListener {
                    playRandom(holder.mPlayerViewView,holder)
                }

                if(clickPosition==position){
                    oldPosition = clickPosition
                    clickPosition = -1
                    holder.playIcon.setBackgroundResource(R.drawable.ic_pause)
                } else if(Constants.position==position){
                    oldPosition = Constants.position
                    Constants.position = -1
                    holder.playIcon.setBackgroundResource(R.drawable.ic_pause)
                }
                else{
                    holder.playIcon.setBackgroundResource(R.drawable.ic_play_circle_outline_black_24dp)
                }
                if(items[position].content!=null&&items[position].content.filePath.contains(".mp3")||items[position].content!=null&&items[position].content.filePath.contains(".m4a")){
                    holder.mPostImageView.visibility=View.VISIBLE
                    holder.mVideoLayout.visibility=View.GONE
                    holder.mVideoView.visibility=View.GONE
                }else if(items[position].content!=null&&items[position].content.filePath.contains(".mp4")){
                    holder.mPostImageView.visibility=View.GONE
                    holder.mVideoView.visibility=View.GONE
                    holder.mVideoLayout.visibility=View.VISIBLE
                    holder.mVideoView.bringToFront()
                }
                if(Constants.isShuffle){
                    holder.randomSongPlayIcon.setColorFilter(ContextCompat.getColor(context, R.color.purple), android.graphics.PorterDuff.Mode.MULTIPLY)
                }else{
                    holder.randomSongPlayIcon.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY)
                }
            }
            catch (e: ClassCastException) {
                throw ClassCastException("Fragment must implement AdapterCallback.")
            }
    }
    // Gets the number of data in the list
    override fun getItemCount(): Int {
        return items.size
    }
    private fun playRandom(video_view: PlayerView,viewHolder: FeedViewHolder){
        val random = Random()
        val songIndex = random.nextInt(items.size)
        if (songIndex >= 0 && items.size>1) {
            try {
                if(items[songIndex].content!=null) {
                    clickListener.randomButtonClick(songIndex, video_view,"http://44.231.47.188" + items[songIndex].content.filePath,viewHolder.playIcon)
                    clickPosition = songIndex
                    notifyDataSetChanged()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }else{
//            refreshSong()
        }
    }
}
interface RecyclerViewClickListener {
    fun onClick( position: Int,video_view: PlayerView,songUrl:String,imageView: ImageView)
    fun stopMediaPlayer( position: Int)
    fun previousButtonClick(position: Int,video_view: PlayerView,songUrl:String,imageView: ImageView)
    fun nextButtonClick(position: Int,video_view: PlayerView,songUrl:String,imageView: ImageView)
    fun randomButtonClick(position: Int,video_view: PlayerView,songUrl:String,imageView: ImageView)
    fun repeatClick(position: Int,video_view: PlayerView,songUrl:String,imageView: ImageView)
}
class FeedViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val playIcon:ImageView=view.play_icon
    val mCountTextView: TextView =view.count_textview
    val nextSongPlayIcon:ImageView=view.next_song_play_icon
    val previousSongPlayIcon:ImageView=view.previous_song_play_icon
    val replaySongPlayIcon:ImageView=view.replay_icon_imageview
    val randomSongPlayIcon:ImageView=view.random_icon_imageview
    val mPostImageView:ImageView=view.backgroundImageView
    val mVideoView:VideoView=view.video_view
    val mPlayerViewView:PlayerView=view.videoFullScreenPlayer
    val mVideoLayout: RelativeLayout =view.view_layout
}
