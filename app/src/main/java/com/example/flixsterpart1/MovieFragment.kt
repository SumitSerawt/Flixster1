package com.example.flixsterpart1

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.ContentLoadingProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.RequestParams
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Headers
import org.json.JSONArray
import org.json.JSONObject

private const val API_KEY = "5e7b3b464eabd6906bc1c9f68bcfccbb"

class MovieFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedIstanceState: Bundle?
    ): View? {

        val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        val layoutId =
            if (isLandscape) R.layout.fragment_movie_list_land else R.layout.fragment_movie_list
        val view = inflater.inflate(layoutId, container, false)
        val progressBar = view.findViewById<View>(R.id.progress) as ContentLoadingProgressBar
        val recyclerView = view.findViewById<View>(R.id.list) as RecyclerView
        val context = view.context
        updateAdapter(progressBar, recyclerView)
        return view
    }

    private fun updateAdapter(progressBar: ContentLoadingProgressBar, recyclerView: RecyclerView) {
        progressBar.show()

        val client = AsyncHttpClient()
        val params = RequestParams()
        params["api_key"] = API_KEY

        client[
            "https://api.themoviedb.org/3/movie/now_playing",
            params,
            object : JsonHttpResponseHandler() {
                override fun onSuccess(
                    statusCode: Int, headers: Headers, json: JsonHttpResponseHandler.JSON
                ) {
                    progressBar.hide()

                    val resultsArray: JSONArray = json.jsonObject.getJSONArray("results")

                    val gson = Gson()
                    val arrayMovieType = object : TypeToken<List<Movie>>() {}.type

                    val models: List<Movie> = gson.fromJson(resultsArray.toString(), arrayMovieType)
                    recyclerView.adapter = MovieRecyclerViewAdapter(
                        models,
                        object : OnListFragmentInteractionListener {
                            override fun onItemClick(item: Movie) {
                                Toast.makeText(context, item.movieName, Toast.LENGTH_LONG).show()
                            }
                        })

                    Log.d("ConnectedMovieAPI", "response successful")

                }

                override fun onFailure(
                    statusCode: Int,
                    headers: Headers?,
                    errorResponse: String,
                    t: Throwable?
                ) {
                    progressBar.hide()
                    t?.message?.let {
                        Log.e("ConnectedMovieAPI", errorResponse)
                    }
                }
            }
        ]
    }

}