package com.puresoftware.bottomnavigationappbar.Weggler.SideFragment.AddCommunity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.bumptech.glide.Glide
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.puresoftware.bottomnavigationappbar.MainActivity
import com.puresoftware.bottomnavigationappbar.R
import com.puresoftware.bottomnavigationappbar.Weggler.Adapter.SelectPicAdapter
import com.puresoftware.bottomnavigationappbar.Weggler.SideFragment.CommunityFragment.ShellFragment
import com.puresoftware.bottomnavigationappbar.databinding.FragmentGallerySlideBinding
import com.sothree.slidinguppanel.SlidingUpPanelLayout

class GallerySlideFragment : Fragment() {
    private var _binding : FragmentGallerySlideBinding? = null
    private val binding get() = _binding!!
    lateinit var mainActivity: MainActivity
    private lateinit var readGalleryListener : PermissionListener

    private var onItemClickListener : OnItemClickListener?= null
    fun setOnItemClickListener(listener: OnItemClickListener){
        this.onItemClickListener = listener
    }
    interface OnItemClickListener{
        fun onItemClick(imageUri:Uri?){}
    }

    var currentUri :Uri? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGallerySlideBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //?????? ?????? ????????? ??????
        readGalleryListener = object :PermissionListener{

            //?????? ??????
            override fun onPermissionGranted() {
                val uriList = getAllShownImagesPath()
                val adapter = SelectPicAdapter(mainActivity,uriList)

                //????????? ?????? ??? ????????? ?????? ????????? ??????
                binding.imageRecycler.adapter = adapter.apply {
                    setOnItemClickListener(object : SelectPicAdapter.OnItemClickListener{
                        override fun onItemClick(imageUri: Uri?) {
                            super.onItemClick(imageUri)
                            currentUri = imageUri
                            if(currentUri==null){
                                binding.selectImage.setImageResource(0)
                            }else {
                                Glide.with(mainActivity)
                                    .load(currentUri)
                                    .into(binding.selectImage)
                            }
                        }
                    })
                }
            }

            //?????? ??????
            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                Toast.makeText(mainActivity,"?????? ??????", Toast.LENGTH_SHORT)
                    .show()
            }

        }

        //????????? ?????? ??????
        TedPermission.with(mainActivity.applicationContext)
            .setPermissionListener(readGalleryListener)
            .setPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).check()

        setUpListener()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    @SuppressLint("Recycle")
    private fun getAllShownImagesPath(): ArrayList<Uri>{
        val uriList = ArrayList<Uri>()
        //?????? ?????? uri
        val uriExternal : Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        var columnIndexId : Int
        var imageId : Long
        val cursor = mainActivity.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null,null,null,
            MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC"
        )
        //?????? ?????? ????????? ????????????
        if (cursor!=null){
            while (cursor.moveToNext()){
                columnIndexId = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                imageId = cursor.getLong(columnIndexId)
                val uriImage = Uri.withAppendedPath(uriExternal, "" + imageId)
                uriList.add(uriImage)
            }
            cursor.close()
        }
        return uriList
    }
    @SuppressLint("ResourceType")
    private fun setUpListener(){
        //????????????
        binding.cancelButton.setOnClickListener {
            onItemClickListener?.onItemClick(currentUri)
        }

        binding.uploadButton.setOnClickListener {
            onItemClickListener?.onItemClick(currentUri)
        }
    }
}