package com.example.mobilefrontend

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.mobilefrontend.databinding.ActivityMainBinding
import com.example.mobilefrontend.itemCard.AdapterClass
import com.example.mobilefrontend.itemCard.DataClass

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var recyclerView: RecyclerView
    private lateinit var dataList: ArrayList<DataClass>

    lateinit var imageList: Array<Int>
    lateinit var cardNameList: Array<String>
    lateinit var cardSetList: Array<String>
    lateinit var cardRarityList: Array<String>
    lateinit var cardCodeList: Array<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFragment(Home())

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    replaceFragment(Home())
                    true
                }
                R.id.nav_users -> {
                    replaceFragment(User())
                    true
                }
                R.id.item_none -> {
                    replaceFragment(Search())
                    true
                }
                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }

    private fun getData() {
        for (i in imageList.indices){
            val dataClass = DataClass(imageList[i], cardNameList[i], cardSetList[i], cardRarityList[i],cardCodeList[i])
            dataList.add(dataClass)
        }

        recyclerView.adapter = AdapterClass(dataList)
    }
}
