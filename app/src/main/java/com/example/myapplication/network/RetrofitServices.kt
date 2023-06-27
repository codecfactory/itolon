package com.example.myapplication.network

import androidx.lifecycle.LiveData
import com.example.myapplication.activities.CreatePlaylistActivity
import com.example.myapplication.model.addcommentsmodel.AddCommentModel
import com.example.myapplication.model.addtoplaylistmodel.AddToPlaylistModel
import com.example.myapplication.model.albumdetailmodel.AlbumDetailModel
import com.example.myapplication.model.allartistmodel.AllArtistModel
import com.example.myapplication.model.albummodel.Album
import com.example.myapplication.model.application.Application
import com.example.myapplication.model.artistdetailmodel.ArtistDetail
import com.example.myapplication.model.cartapimodel.CartApiModel
import com.example.myapplication.model.categoriessongmodel.CategoriesSongModel
import com.example.myapplication.model.categorymodel.CategoryModel
import com.example.myapplication.model.createplaylistmodel.CreatePlaylistModel
import com.example.myapplication.model.defaultmodel.DefaultModel
import com.example.myapplication.model.deleteartistsongmodel.DeleteSong
import com.example.myapplication.model.loginmodel.Login
import com.example.myapplication.model.playlistdetailmodel.PlaylistDetail
import com.example.myapplication.model.playlistmodel.PlaylistModel
import com.example.myapplication.model.postmodel.Post
import com.example.myapplication.model.postsmodel.Posts
import com.example.myapplication.model.profilemodel.Profile
import com.example.myapplication.model.screenimagesmodel.ScreenImages
import com.example.myapplication.model.signupmodell.Signup
import com.example.myapplication.model.starsmodel.StarsModel
import com.example.myapplication.model.userdownloadsmodel.UserDownloadModel
import retrofit2.http.*

interface RetrofitServices {
    @FormUrlEncoded
    @POST("/api/signUp/phoneNumber")
    fun signupUser(@FieldMap hashMap: HashMap<String, String>): LiveData<ApiResponse<Signup>>

    @FormUrlEncoded
    @POST("/api/signUp/verifyPhone")
    fun verifyUser(@FieldMap hashMap: HashMap<String, String>): LiveData<ApiResponse<Signup>>

    @FormUrlEncoded
    @POST("/api/LogIn/Phone")
    fun loginUser(@FieldMap hashMap: HashMap<String, String>): LiveData<ApiResponse<Login>>

    @POST("/api/posts")
    fun getPosts(@QueryMap hashMap: HashMap<String, String>): LiveData<ApiResponse<Posts>>

    @POST("/api/content")
    fun getHomePosts(@QueryMap hashMap: HashMap<String, String>): LiveData<ApiResponse<Post>>

    @POST("/api/artist/content/delete")
    fun deleteContent(@QueryMap hashMap: HashMap<String, String>): LiveData<ApiResponse<DeleteSong>>

    @POST("/api/albums")
    fun getAlbums(@QueryMap hashMap: HashMap<String, String>): LiveData<ApiResponse<Album>>

    @POST("/api/content/categories")
    fun getArtistSongCategories(@QueryMap hashMap: HashMap<String, String>): LiveData<ApiResponse<CategoryModel>>

    @POST("/api/user/profile")
    fun getUserProfile(@QueryMap hashMap: HashMap<String, String>): LiveData<ApiResponse<Profile>>

    @POST("/api/applicationForm")
    fun submitApplication(@QueryMap hashMap: HashMap<String, String>): LiveData<ApiResponse<Application>>

    @POST("/api/user/artists")
    fun getUserArtists(@QueryMap hashMap: HashMap<String, String>): LiveData<ApiResponse<Application>>

    @POST("/api/user/star_artists")
    fun getStarArtists(@QueryMap hashMap: HashMap<String, String>): LiveData<ApiResponse<StarsModel>>

    @POST("/api/user/playlists")
    fun getPlaylists(@QueryMap hashMap: HashMap<String, String>): LiveData<ApiResponse<PlaylistModel>>

    @POST("/api/user/add_song_playlist")
    fun addToPlaylist(@QueryMap hashMap: HashMap<String, String>): LiveData<ApiResponse<AddToPlaylistModel>>

    @POST("api/user/all-artists")
    fun getAllArtists(@QueryMap hashMap: HashMap<String, String>): LiveData<ApiResponse<AllArtistModel>>

    @POST("api/artist_detail")
    fun getArtistDetails(@QueryMap hashMap: HashMap<String, String>): LiveData<ApiResponse<ArtistDetail>>

    @POST("api/album/detail")
    fun getAlbumDetails(@QueryMap hashMap: HashMap<String, String>): LiveData<ApiResponse<AlbumDetailModel>>

    @POST("/api/user/downloads")
    fun getUserDownloads(@QueryMap hashMap: HashMap<String, String>): LiveData<ApiResponse<UserDownloadModel>>

    @POST("/api/user/playlist_songs")
    fun getPlaylistDetail(@QueryMap hashMap: HashMap<String, String>): LiveData<ApiResponse<PlaylistDetail>>

    @POST("/api/user/categories_songs")
    fun getCategoriesSongs(@QueryMap hashMap: HashMap<String, String>): LiveData<ApiResponse<CategoriesSongModel>>

    @POST("/api/user/create_update_playlist")
    fun createPlaylist(@QueryMap hashMap: HashMap<String, String>): LiveData<ApiResponse<CreatePlaylistModel>>

    @POST("/api/auth/logout")
    fun logoutUser(@QueryMap hashMap: HashMap<String, String>): LiveData<ApiResponse<DefaultModel>>

    @POST("/api/post/comment")
    fun addComments(@QueryMap hashMap: HashMap<String, String>): LiveData<ApiResponse<AddCommentModel>>

    @POST("/api/user/song_downloaded")
    fun addDownload(@QueryMap hashMap: HashMap<String, String>): LiveData<ApiResponse<DefaultModel>>

    @POST("/api/user/update")
    fun updateUSer(@QueryMap hashMap: HashMap<String, String>): LiveData<ApiResponse<DefaultModel>>

    @POST("/api/user/add-to-cart")
    fun addToCart(@QueryMap hashMap: HashMap<String, String>): LiveData<ApiResponse<CartApiModel>>

    @POST("/api/ads")
    fun getAds(): LiveData<ApiResponse<DefaultModel>>

    @POST("/api/screen/images?screen_type=home")
    fun getScreenImages(): LiveData<ApiResponse<ScreenImages>>
}