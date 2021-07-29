package com.example.bottomsheet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject

/* !! 깃에다가 올리기
1.버튼 클릭시 다이얼로그 띄우기
2.데이터 제대로 가져오는지 로그 찍어서 확인
3. 데이터 잘 오면, 화면에 뿌려서 화면 그리기 -> orderList = "[{"code":"CARD","display":"Y","name":"카드 홈","order":1},{"code":"WALLET","display":"Y","name":"월렛","order":2},{"code":"ASSET","display":"Y","name":"자산","order":3},{"code":"DISCOVER","display":"Y","name":"디스커버","order":4},{"code":"TIMELINE","display":"Y","name":"알림","order":5}]"
-> display 가 Y 면 arrayItemAdded, N 이면 arrayItemDeleted 에 담고, adapter 에 매개변수로 array 넘겨서 뿌리기
4. 그 다음에 드래그 해보기, adapter 붙이고, adapter 만들기
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_dialog.setOnClickListener {
            val bottomSheetEditMyPage = BottomSheetEditMyPageTest().apply {
                this.show(supportFragmentManager, this.tag)
            }
        }
    }
}