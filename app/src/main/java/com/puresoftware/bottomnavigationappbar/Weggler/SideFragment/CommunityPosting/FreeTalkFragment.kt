package com.puresoftware.bottomnavigationappbar.Weggler.SideFragment.CommunityPosting

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import com.puresoftware.bottomnavigationappbar.MainActivity
import com.puresoftware.bottomnavigationappbar.Weggler.Adapter.ItemCommunitySmallAdapterFree
import com.puresoftware.bottomnavigationappbar.Weggler.Model.ReviewInCommunity
import com.puresoftware.bottomnavigationappbar.databinding.FragmentFreeTalkBinding

//프리 토크

class FreeTalkFragment(
    ) : Fragment() {
    private var selectPosition: String? =null
    private var _binding : FragmentFreeTalkBinding? = null
    private val binding get()=_binding!!
    private lateinit var mainActivity: MainActivity
    private lateinit var fm: FragmentManager

    var data : ArrayList<ReviewInCommunity> = arrayListOf()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
        fm = mainActivity.supportFragmentManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            selectPosition = it.getString("selectPosition",null)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        _binding=FragmentFreeTalkBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // 어댑터 생성 + 클릭 이벤트 적용
        val adapter = ItemCommunitySmallAdapterFree(mainActivity, data)
        binding.totalRecycler.adapter = adapter.apply {
            setOnItemClickListener(object : ItemCommunitySmallAdapterFree.OnItemClickListener{
                override fun onItemClick(item: ReviewInCommunity) {
                    mainActivity.changeFragment(DetailCommunityPostingFragment.newInstance(item.reviewId,"sub"))
                }

            })
        }

        // 게시물 데이터 설정 (옵저버로 관찰)
        mainActivity.communityViewModel.apply {
            // 메인 포스팅
            if (selectPosition == "Main Posting") {
                data  = if(communityLiveData.value==null) arrayListOf() else  communityLiveData.value!!
                communityLiveData.observe(mainActivity, Observer {
                    adapter.setData(it)
                })

                // 인기 게시물
            } else if (selectPosition == "Popular Posting") {
                data  = if(popularPostingLiveData.value==null) arrayListOf() else  popularPostingLiveData.value!!
                popularPostingLiveData.observe(mainActivity, Observer {
                    adapter.setData(it)
                })

                // 내 게시물
            } else if (selectPosition == "My Posting"){
                data  = if(myPostingLiveData.value==null) arrayListOf() else  myPostingLiveData.value!!
                communityLiveData.observe(mainActivity, Observer {
                    adapter.setData(it)
                })

                //내 댓글
            } else if (selectPosition== "My Comment"){
                Log.d("dljsdlfj sdflsjdfos","내 댓글 리스트 입니다 .~~~")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object{
        fun newInstance(selectPosition:String) =
            FreeTalkFragment().apply {
                arguments= Bundle().apply {
                    putString("selectPosition",selectPosition)
                }
            }

    }

}