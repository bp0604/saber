package com.light.saber.crawler

import com.light.saber.dao.ImageRepository
import com.light.saber.model.Image
import com.light.saber.util.GankImageJsonResultProcessor
import com.light.saber.util.URLUtil
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.util.*

@Service
class CrawImageServiceGankImpl : CrawImageService {
    val logger = LoggerFactory.getLogger(CrawImageServiceGankImpl::class.java)

    @Autowired
    lateinit var imageRepository: ImageRepository

    override fun doCrawJob() = runBlocking {
        for (page in 1..7) {
            launch(CommonPool) {
                saveImage(page)
            }
        }
    }

    override fun saveImage(page: Int) {
        GankImageJsonResultProcessor.getGankImageUrls(page).forEach {
            val url = it
            if (imageRepository.countByUrl(url) == 0) {
                val image = Image()
                image.category = "干货集中营福利 ${SimpleDateFormat("yyyy-MM-dd").format(Date())}"
                image.url = url
                image.sourceType = 1
                image.imageBlob = URLUtil.getByteArray(url)
                logger.info("image = ${image}")
                imageRepository.save(image)
            }
        }
    }

}
