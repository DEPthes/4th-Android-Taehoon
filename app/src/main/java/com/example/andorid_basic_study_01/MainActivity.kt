package com.example.andorid_basic_study_01

import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.andorid_basic_study_01.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var input = ""
    private var result = ""
    private var LastOperator = ""

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setButtonListeners()


    }
    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    private fun setButtonListeners() {
        // 숫자 버튼들
        val numbers = listOf(
            binding.bt0,
            binding.bt1,
            binding.bt2,
            binding.bt3,
            binding.bt4,
            binding.bt5,
            binding.bt6,
            binding.bt7,
            binding.bt8,
            binding.bt9,
            binding.btDot
        )

        numbers.forEach { button ->
            button.setOnClickListener {
                val buttonText = button.text.toString()
                input += buttonText
                binding.tvResult1.text = input
            }
        }

        val operators = listOf(
            binding.btPlus,
            binding.btMinus,
            binding.btX,
            binding.btN,
            binding.btPer,
            binding.btBrak
        )

        operators.forEach { button ->
            button.setOnClickListener {
                when(LastOperator) {
                    "+" -> plus()
                    "-" -> minus()
                    "X" -> X()
                    "/" -> division()
                    else -> FirstInput()
                }

                LastOperator = button.text.toString()
            }
        }

        // C 버튼
        binding.btC.setOnClickListener {
            input = ""
            result = ""
            binding.tvResult1.text = "0"
            binding.tvResult2.text = "0"
        }

        // "=" 버튼
        binding.btEqual.setOnClickListener {
            when(LastOperator) {
                "+" -> plus()
                "-" -> minus()
                "X" -> X()
                "/" -> division()
                else -> FirstInput()
            }
            binding.tvResult1.text = result
            input = result
            result = ""
        }
    }

    fun plus() {
            val x = result.toInt() + input.toInt() //지역변수에 계산값
            result = x.toString() //result의 계산값 저장
            input = "" //input초기화
            binding.tvResult1.text = "0" //view result1 초기화
            binding.tvResult2.text = result //vew result2 는 계속 더한 결괏값 추가

    }

    fun minus() {
            val x = result.toInt() - input.toInt() //지역변수에 계산값
            result = x.toString() //result의 계산값 저장
            input = "" //input초기화
            binding.tvResult1.text = "0" //view result1 초기화
            binding.tvResult2.text = result //vew result2 는 계속 더한 결괏값 추가

    }

    fun X() {
        val x = result.toInt() * input.toInt() //지역변수에 계산값
        result = x.toString() //result의 계산값 저장
        input = "" //input초기화
        binding.tvResult1.text = "0" //view result1 초기화
        binding.tvResult2.text = result //vew result2 는 계속 더한 결괏값 추가
    }

    fun division() {
        val x = result.toInt() / input.toInt() //지역변수에 계산값
        result = x.toString() //result의 계산값 저장
        input = "" //input초기화
        binding.tvResult1.text = "0" //view result1 초기화
        binding.tvResult2.text = result //vew result2 는 계속 더한 결괏값 추가
    }

    fun FirstInput() {
        result = input //result설정 후
        binding.tvResult2.text = result //tvResult2 view상 text result값으로 추가
        input = "" //작업이 끝나면 input초기화
        binding.tvResult1.text = "0" // result1도 같이 초기화
    }



}
