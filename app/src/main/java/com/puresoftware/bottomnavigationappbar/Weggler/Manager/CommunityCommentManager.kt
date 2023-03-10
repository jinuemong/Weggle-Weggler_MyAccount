package com.puresoftware.bottomnavigationappbar.Weggler.Manager

import com.puresoftware.bottomnavigationappbar.Weggler.Model.BodyComment
import com.puresoftware.bottomnavigationappbar.Weggler.Model.Comment
import com.puresoftware.bottomnavigationappbar.Weggler.Model.CommentList
import com.puresoftware.bottomnavigationappbar.Server.MasterApplication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CommunityCommentManager(
    private val wApp : MasterApplication
) {

    //내가 쓴  댓글 조회
    fun getMyCommentList(paramFunc:(ArrayList<Comment>?,String?)->Unit){
        wApp.service.getMyCommentList(null,null,null)
            .enqueue(object : Callback<CommentList>{
                override fun onResponse(call: Call<CommentList>, response: Response<CommentList>) {
                    if (response.isSuccessful){
                        paramFunc(response.body()!!.content,null)
                    }else{
                        paramFunc(null, response.errorBody()!!.string())
                    }
                }

                override fun onFailure(call: Call<CommentList>, t: Throwable) {
                    paramFunc(null,"error")
                }
            })
    }

    //리뷰 아이디로 댓글 가져오기
    fun getReviewCommentList(reviewId : Int,paramFunc: (ArrayList<Comment>?, String?) -> Unit){
        wApp.service.getReviewCommentList(reviewId,null,null,null)
            .enqueue(object : Callback<CommentList>{
                override fun onResponse(call: Call<CommentList>, response: Response<CommentList>) {
                    if (response.isSuccessful){
                        paramFunc(response.body()!!.content,null)
                    }else{
                        paramFunc(null, response.errorBody()!!.string())
                    }
                }

                override fun onFailure(call: Call<CommentList>, t: Throwable) {
                    paramFunc(null,"error")
                }

            })
    }

    //댓글 추가하기
    fun addReviewComment(reviewId: Int, body:String,paramFunc: (Comment?, String?) -> Unit){
        wApp.service.addReviewComment(reviewId, BodyComment(body))
            .enqueue(object :Callback<Comment>{
                override fun onResponse(call: Call<Comment>, response: Response<Comment>) {
                    if (response.isSuccessful){
                        paramFunc(response.body(),null)
                    }else{
                        paramFunc(null,response.errorBody()!!.string())
                    }
                }

                override fun onFailure(call: Call<Comment>, t: Throwable) {
                    paramFunc(null,"error")
                }

            })
    }
}