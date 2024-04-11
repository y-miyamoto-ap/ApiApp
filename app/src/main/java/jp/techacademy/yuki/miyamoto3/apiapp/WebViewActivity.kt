package jp.techacademy.yuki.miyamoto3.apiapp

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import jp.techacademy.yuki.miyamoto3.apiapp.databinding.ActivityWebViewBinding

const val EXTRA_SHOP = "jp.techacademy.yuki.miyamoto3.apiapp.SHOP"

class WebViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWebViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val shop = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(EXTRA_SHOP, Shop::class.java)
        } else {
            intent.getSerializableExtra(EXTRA_SHOP)
        } as Shop

        // nameTextViewのtextプロパティに代入されたオブジェクトのnameプロパティを代入
        binding.nameTextView.text = shop.name

        var isFavorite = FavoriteShop.findBy(shop.id) != null
        if (isFavorite) binding.favoriteImageView.setImageResource(R.drawable.ic_star)

        val couponUrl = shop.couponUrls.sp.ifEmpty { shop.couponUrls.pc }
        binding.webView.loadUrl(couponUrl)

        // 星をタップした時の処理
        binding.favoriteImageView.setOnClickListener {
            if (isFavorite) {
                AlertDialog.Builder(this)
                    .setTitle(R.string.delete_favorite_dialog_title)
                    .setMessage(R.string.delete_favorite_dialog_message)
                    .setPositiveButton(android.R.string.ok) { _, _ ->
                        FavoriteShop.delete(shop.id)
                        binding.favoriteImageView.setImageResource(R.drawable.ic_star_border)
                        isFavorite = false
                    }
                    .setNegativeButton(android.R.string.cancel) { _, _ -> }
                    .create()
                    .show()
            } else {
                FavoriteShop.insert(FavoriteShop().apply {
                    id = shop.id
                    name = shop.name
                    imageUrl = shop.logoImage
                    url = couponUrl
                })
                binding.favoriteImageView.setImageResource(R.drawable.ic_star)
                isFavorite = true
            }
        }
    }

    companion object {
        fun start(activity: Activity, shop:Shop) {
            activity.startActivity(
                Intent(activity, WebViewActivity::class.java).putExtra(EXTRA_SHOP, shop)
            )
        }
    }
}