package com.puresoftware.bottomnavigationappbar.SideMenu

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import com.puresoftware.bottomnavigationappbar.LoginActivityTemporary
import com.puresoftware.bottomnavigationappbar.MainActivity
import com.puresoftware.bottomnavigationappbar.databinding.FragmentSettingBinding

class SettingFragment : Fragment() {
    private lateinit var mainActivity : MainActivity
    private var _binding : FragmentSettingBinding? = null
    private val binding get() = _binding!!
    private lateinit var  callback : OnBackPressedCallback

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
        callback =object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                mainActivity.setSubFragment()
            }

        }
        requireActivity().onBackPressedDispatcher.addCallback(this,callback)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater,container,false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpListener()
    }


    private fun setUpListener(){
        binding.backButton.setOnClickListener {
            mainActivity.setSubFragment()
        }
        binding.logout.setOnClickListener {
            // 로그아웃
            val intent = Intent(mainActivity, LoginActivityTemporary::class.java)
            intent.putExtra("logout",true)
            startActivity(intent)
        }
    }

}