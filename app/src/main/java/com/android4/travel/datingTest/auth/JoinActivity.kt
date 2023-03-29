package com.android4.travel.datingTest.auth

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.android4.travel.MainActivity
import com.android4.travel.R
import com.android4.travel.databinding.ActivityJoinBinding
import com.android4.travel.datingTest.utils.FirebaseRef
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream

class JoinActivity : AppCompatActivity() {
    private  val TAG = "JoinActivity"
    private lateinit var auth: FirebaseAuth
    lateinit var binding: ActivityJoinBinding


    // 닉네임 성별 지역 나이 UID
    private var nickname = ""
    private var gender = ""
    private var city = ""
    private var age = ""
    private var uid = ""

    lateinit var profileImage : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        binding= ActivityJoinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        //회원가입 액티비티 화면에서 해당 프로필 이미지 선택.
        profileImage = findViewById(R.id.imageArea)

        // 갤러리 화면에서 해당 주소에서 이미지 선택 후 가져오는 알고리즘.
        val getAction = registerForActivityResult(
            ActivityResultContracts.GetContent(),
            ActivityResultCallback { uri ->
                profileImage.setImageURI(uri)
            }
        )

        //이미지 영역 클릭시 갤러리 열기.
        profileImage.setOnClickListener {
            getAction.launch("image/*")
        }


        //회원가입 버튼 클릭시 이벤트 진행.
        val joinBtn = findViewById<Button>(R.id.joinBtn)
        joinBtn.setOnClickListener {

            val email = findViewById<TextInputEditText>(R.id.emailArea)
            val pwd = findViewById<TextInputEditText>(R.id.pwdArea)

            gender = findViewById<TextInputEditText>(R.id.genderArea).text.toString()
            city = findViewById<TextInputEditText>(R.id.cityArea).text.toString()
            age = findViewById<TextInputEditText>(R.id.ageArea).text.toString()
            nickname = findViewById<TextInputEditText>(R.id.nicknameArea).text.toString()

//            Log.d("lsy", email.text.toString())
//            Log.d("lsy", pwd.text.toString())

            // 이메일과 패스워드로 인증 만들기.
            auth.createUserWithEmailAndPassword(email.text.toString(), pwd.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
//                        Log.d("lsy", "createUserWithEmail:success")
//                        val user = auth.currentUser
//                        Log.d("lsy", user?.uid.toString())

                        //파이어베이스 인증된 현재 유저를 담은 객체
                        val user = auth.currentUser
                        // 유저에 담긴 uid 문자열로 저장.
                        uid = user?.uid.toString()

                        // 토큰 받아오는 샘플.
                        FirebaseMessaging.getInstance().token.addOnCompleteListener(
                            OnCompleteListener { task ->
                                if (!task.isSuccessful) {
                                    Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                                    return@OnCompleteListener
                                }

                                // Get new FCM registration token
                                val token = task.result

                                // Log and toast
                                Log.e(TAG, token.toString())

                                // 유저 정보를 담는 객체 생성, 매개변수 5개
                                // 각 변수는 위에 참고.
                                val userModel = UserDataModel(
                                    uid,
                                    nickname,
                                    age,
                                    gender,
                                    city,
                                    token
                                )

                                //파이어베이스 데이터 쓰기 부분을 따로 파일 분리.
                                // FirebaseRef 클래스에 공유자원처럼 정의.
                                // userInfoRef 여기 변수에  database.getReference("userInfo")
                                // 파이어베이스 실시간 데이터베이스에 userInfo 컬렉션 하위에
                                // 각 유저의 정보를 저장. age,city,gender,nickname,uid
                                // userInfo -> 하위에 uid 생성후 -> 하위에 각 정보 5개가 저장되는 구조.
                                FirebaseRef.userInfoRef.child(uid).setValue(userModel)

//                        이미지 업로드
                                uploadImage(uid)

                                // 회원가입 후 메인으로
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)

                            })




                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("lsy", "createUserWithEmail:failure", task.exception)

                    }
                }

        }


    }
    // 이미지 업로드 함수: uid 이름으로 파일 이미지 저장.
    private fun uploadImage(uid : String){

        //스토리지 객체 생성
        val storage = Firebase.storage
        // 스토리지 하위에 uid명.png 형식으로 저장.
        val storageRef = storage.reference.child(uid + ".png")


        // Get the data from an ImageView as bytes
        profileImage.isDrawingCacheEnabled = true
        profileImage.buildDrawingCache()
        val bitmap = (profileImage.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        var uploadTask = storageRef.putBytes(data)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
        }.addOnSuccessListener { taskSnapshot ->
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...
        }


    }
}