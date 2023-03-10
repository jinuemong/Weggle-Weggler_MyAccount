package com.puresoftware.bottomnavigationappbar.Weggler

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.tabs.TabLayout
import com.puresoftware.bottomnavigationappbar.MainActivity
import com.puresoftware.bottomnavigationappbar.R
import com.puresoftware.bottomnavigationappbar.Weggler.Manager.CommunityCommentManager
import com.puresoftware.bottomnavigationappbar.Weggler.Manager.CommunityManagerWithReview
import com.puresoftware.bottomnavigationappbar.Weggler.MainFragment.ChallengeFragment
import com.puresoftware.bottomnavigationappbar.Weggler.MainFragment.CommunityFragment
import com.puresoftware.bottomnavigationappbar.Weggler.MainFragment.FeedFragment
import com.puresoftware.bottomnavigationappbar.Weggler.MainFragment.RankingFragment
import com.puresoftware.bottomnavigationappbar.Weggler.Manager.ProductManager
import com.puresoftware.bottomnavigationappbar.databinding.WegglerFragmentBinding


class WegglerFragment : Fragment() {
    private var _binding : WegglerFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainActivity:MainActivity
    private lateinit var fm : FragmentManager

    private lateinit var productManager : ProductManager
    private lateinit var communityPostManager:  CommunityManagerWithReview
    private lateinit var communityCommentManager : CommunityCommentManager

    private var feedFragment: FeedFragment? = null
    private var challengeFragment: ChallengeFragment? = null
    private var communityFragment: CommunityFragment? = null
    private var rankingFragment: RankingFragment? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity  = context as MainActivity
        fm = mainActivity.supportFragmentManager
        productManager = ProductManager(mainActivity.masterApp)
        communityPostManager = CommunityManagerWithReview(mainActivity.masterApp)
        communityCommentManager = CommunityCommentManager(mainActivity.masterApp)

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = WegglerFragmentBinding.inflate(inflater,container,false)
        return binding.root
    }

    @SuppressLint("CommitPrefEdits")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initServerData()
        feedFragment = FeedFragment()
        challengeFragment = ChallengeFragment()
        communityFragment = CommunityFragment()
        rankingFragment = RankingFragment()

        midFragmentChange(feedFragment!!)
        binding.wegglerTab.addOnTabSelectedListener(object :  TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position){
                    0 ->{midFragmentChange(feedFragment!!)}
                    1 ->{midFragmentChange(challengeFragment!!)}
                    2 ->{midFragmentChange(communityFragment!!)}
                    3 ->{midFragmentChange(rankingFragment!!)}
                    else ->{midFragmentChange(feedFragment!!)}
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}

        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        feedFragment = null
        challengeFragment = null
        communityFragment = null
        rankingFragment = null
    }

    private fun midFragmentChange(goFragment:Fragment){
        fm.beginTransaction().replace(R.id.mid_container,goFragment)
            .commit()
    }


    private fun initServerData(){
        productManager.initCommunityProduct { communityList, message ->
            Log.d("sldfjowejowfe 1",communityList.toString()+","+message.toString())
            if (message==null){
                mainActivity.communityViewModel.communityProduct = communityList
                // ??? ?????? ????????? ???????????? ( ???????????? + ???????????? ???????????? body -type?????? ?????? )
                communityPostManager.getCommunityReviewList(communityList!!.productId, paramFunc = { data, message2->
                    Log.d("sldfjowejowfe 2",communityList.toString()+","+message2.toString())

                    if (message2==null){
                        if (data != null) {
                            mainActivity.communityViewModel.setCommunityData(data)
                        }
                    }else{
                        Toast.makeText(mainActivity, message2, Toast.LENGTH_SHORT).show()
                        Log.d("sldfjowejowfe 2",communityList.toString()+","+message2.toString())

                    }
                })

                // ??? ????????? ????????????
                communityPostManager.getMyCommunityReviewList(paramFunc = { data, message2 ->
                    Log.d("sldfjowejowfe 3",communityList.toString()+","+message2.toString())

                    if (message2==null){
                        if (data != null) {
                            mainActivity.communityViewModel.setMyPostingData(data)
                        }
                    }else{
                        Toast.makeText(mainActivity, message2, Toast.LENGTH_SHORT).show()
                        Log.d("sldfjowejowfe 3",communityList.toString()+","+message2.toString())

                    }
                })

                // ?????? ????????? ????????????
                communityPostManager.getCommunityReviewListByLike(communityList.productId, paramFunc = { data,message2->
                    Log.d("sldfjowejowfe 4",communityList.toString()+","+message2.toString())

                    if (message2 == null) {
                        if (data != null) {
                            mainActivity.communityViewModel.setPopularPostingData(data)
                        }
                    } else {
                        Toast.makeText(mainActivity, message2, Toast.LENGTH_SHORT).show()
                        Log.d("sldfjowejowfe 4",communityList.toString()+","+message2.toString())

                    }
                })

                //??? ?????? ????????????
                communityCommentManager.getMyCommentList(paramFunc = { data,message2->
                    Log.d("sldfjowejowfe 5",communityList.toString()+","+message.toString())
                    if (message2==null){
                        if( data!=null){
                            mainActivity.communityViewModel.setMyCommentData(data)
                        }
                    }else{
                        Toast.makeText(mainActivity, message2, Toast.LENGTH_SHORT).show()
                        Log.d("sldfjowejowfe 5",communityList.toString()+","+message.toString())

                    }
                })
            }else{
                Toast.makeText(mainActivity, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

}