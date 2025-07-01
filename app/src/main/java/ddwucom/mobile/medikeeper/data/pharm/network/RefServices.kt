package ddwucom.mobile.medikeeper.data.pharm.network

import ddwucom.mobile.medikeeper.R
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RefServices(private val pharmacyService: IPharmacyService) {

    companion object {
        fun create(baseUrl: String): RefServices {
            // Logging interceptor
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            // OkHttpClient 설정
            val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build()

            // Retrofit 인스턴스 생성
            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            // IPharmacyService 생성
            val pharmacyService = retrofit.create(IPharmacyService::class.java)

            return RefServices(pharmacyService)
        }
    }

    suspend fun getPharmacy(key: String): List<ModelProperties> {
        val definitions: Pharamcy = pharmacyService.getPharmacy(key, 1, 5)
        return definitions.data
    }
}
