package ru.skillbranch.skillarticles

import android.Manifest
import android.net.Uri
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.testing.launchFragment
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.internal.notify
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import ru.skillbranch.skillarticles.data.local.PrefManager
import ru.skillbranch.skillarticles.data.models.User
import ru.skillbranch.skillarticles.data.repositories.ProfileRepository
import ru.skillbranch.skillarticles.ui.RootActivity
import ru.skillbranch.skillarticles.ui.profile.ProfileFragment
import ru.skillbranch.skillarticles.viewmodels.profile.PendingAction
import ru.skillbranch.skillarticles.viewmodels.profile.ProfileViewModel
import java.io.FileNotFoundException


val avatarUploadRes: String = """{
    "url":"https://skill-branch.ru/avatar.jpg"
    }
""".trimIndent()

val avatarRemoveRes: String = """{
    "url":""
    }
""".trimIndent()

val profileEditRes: String = """{
    "id": "test_id",
    "name": "edit test name",
    "avatar": "https://skill-branch.ru/avatar.jpg",
    "rating": 0,
    "respect": 0,
    "about": "edit something about"
    }
""".trimIndent()

@RunWith(AndroidJUnit4::class)
class InstrumentalTest1 {
    private lateinit var server: MockWebServer
    private val profileRepository = ProfileRepository

    @Captor
    private lateinit var argCaptor: ArgumentCaptor<Map<String, Pair<Boolean, Boolean>>>

    companion object {
        @BeforeClass
        @JvmStatic
        fun setup() {
            AppConfig.BASE_URL = "http://localhost:8080/"
        }
    }

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this);
        server = MockWebServer()
        server.start(8080)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun upload_avatar() {
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(avatarUploadRes)
        )

        runBlocking {
            PrefManager.accessToken = "Bearer test_access_token"
            val reqFile: RequestBody = "test".toRequestBody("image/jpeg".toMediaType())
            val body: MultipartBody.Part =
                MultipartBody.Part.createFormData("avatar", "name.jpg", reqFile)
            profileRepository.uploadAvatar(body)

            Assert.assertEquals("https://skill-branch.ru/avatar.jpg", PrefManager.profile?.avatar)

            val recordedRequest = server.takeRequest();

            Assert.assertEquals("POST", recordedRequest.method)
            Assert.assertEquals("/profile/avatar/upload", recordedRequest.path)
            Assert.assertEquals(
                "Bearer test_access_token",
                recordedRequest.headers["Authorization"]
            )
            Assert.assertEquals(218, recordedRequest.body.size)
        }
    }

    @Test
    fun remove_avatar() {
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(avatarRemoveRes)
        )
        runBlocking {
            PrefManager.accessToken = "Bearer test_access_token"
            profileRepository.removeAvatar()

            Assert.assertEquals("", PrefManager.profile?.avatar)

            val recordedRequest = server.takeRequest();

            Assert.assertEquals("PUT", recordedRequest.method)
            Assert.assertEquals("/profile/avatar/remove", recordedRequest.path)
            Assert.assertEquals(
                "Bearer test_access_token",
                recordedRequest.headers["Authorization"]
            )
        }
    }

    @Test
    fun edit_profile() {
        val expectedUser = User(
            id = "test_id",
            name = "test name",
            avatar = "https://skill-branch.ru/avatar.jpg",
            rating = 0,
            respect = 0,
            about = "something about"
        )
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(profileEditRes)
        )

        runBlocking {
            PrefManager.accessToken = "Bearer test_access_token"
            PrefManager.profile = expectedUser
            Assert.assertEquals(expectedUser, PrefManager.profile)

            profileRepository.editProfile("edit test name", "edit something about")
            Assert.assertEquals(
                expectedUser.copy(
                    name = "edit test name",
                    about = "edit something about"
                ), PrefManager.profile
            )
            val recordedRequest = server.takeRequest();

            Assert.assertEquals("PUT", recordedRequest.method)
            Assert.assertEquals("/profile", recordedRequest.path)
            Assert.assertEquals(
                "Bearer test_access_token",
                recordedRequest.headers["Authorization"]
            )
            Assert.assertEquals(
                "[text={\"name\":\"edit test name\",\"about\":\"edit something about\"}]",
                recordedRequest.body.toString()
            )
        }
    }

    @Test
    fun prepare_and_delete_uri() {
        val expectedPath =
            "content://ru.skillbranch.skillarticles.provider/external_files/Android/data/ru.skillbranch.skillarticles/files/Pictures"
        val mockRoot = mock(
            RootActivity::class.java,
            withSettings().defaultAnswer(RETURNS_DEEP_STUBS)
        )

        with(launchFragment(themeResId = R.style.AppTheme) {
            ProfileFragment(mockRoot)
        }) {
            onFragment { fragment ->
                val uri = fragment.prepareTempUri()
                assertEquals(expectedPath, uri.toString().split("/").dropLast(1).joinToString("/"))
                fragment.removeTempUri(uri)
                try {
                    fragment.requireContext().contentResolver.openInputStream(uri)
                }catch (e: Throwable){
                    assertEquals(true, e is FileNotFoundException)
                }

            }
        }
    }

    @Test
    fun request_permissions() {
        val expectedResult = mapOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE to Pair(true , true),
            Manifest.permission.READ_EXTERNAL_STORAGE to Pair(true , true)
        )
        val testRegistry = object : ActivityResultRegistry() {
            override fun <I, O> onLaunch(
                requestCode: Int,
                contract: ActivityResultContract<I, O>,
                input: I,
                options: ActivityOptionsCompat?
            ) {
                dispatchResult(requestCode, expectedResult.mapValues { true })
            }
        }
        val mockRoot = mock(
            RootActivity::class.java,
            withSettings().defaultAnswer(RETURNS_DEEP_STUBS)
        )

        with(launchFragment(themeResId = R.style.AppTheme) {
            ProfileFragment(mockRoot, testRegistry, { MockViewModelFactory(it)} )
        }) {
            onFragment { fragment ->
                // Trigger the ActivityResultLauncher
                fragment.viewModel.requestPermissions(expectedResult.keys.toList())
                // Verify the result is set
                verify(fragment.viewModel).handlePermission(capture(argCaptor))

                assertEquals(expectedResult, argCaptor.value)
                verify(fragment.viewModel).executePendingAction()

            }
        }

    }

    @Test
    fun request_no_permissions() {
        val expectedResult = mapOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE to Pair(false , false),
            Manifest.permission.READ_EXTERNAL_STORAGE to Pair(false , false)
        )
        val testRegistry = object : ActivityResultRegistry() {
            override fun <I, O> onLaunch(
                requestCode: Int,
                contract: ActivityResultContract<I, O>,
                input: I,
                options: ActivityOptionsCompat?
            ) {
                dispatchResult(requestCode, expectedResult.mapValues { false })
            }
        }
        val mockRoot = mock(
            RootActivity::class.java,
            withSettings().defaultAnswer(RETURNS_DEEP_STUBS)
        )
//        val args = argumentCaptor<Map<String, Pair<Boolean, Boolean>>>()

        with(launchFragment(themeResId = R.style.AppTheme) {
            ProfileFragment(mockRoot, testRegistry,{ MockViewModelFactory(it)} )
        }) {
            onFragment { fragment ->

                // Trigger the ActivityResultLauncher
                fragment.viewModel.requestPermissions(expectedResult.keys.toList())
                // Verify the result is set

                verify(fragment.viewModel).handlePermission(capture(argCaptor))
                assertEquals(expectedResult, argCaptor.value)
                verify(fragment.viewModel, never()).executePendingAction()
                verify(fragment.viewModel).executeOpenSettings()

            }
        }

    }


    @Test
    fun take_result_from_camera() {

        val testRegistry = object : ActivityResultRegistry() {
            override fun <I, O> onLaunch(
                requestCode: Int,
                contract: ActivityResultContract<I, O>,
                input: I,
                options: ActivityOptionsCompat?
            ) {
                dispatchResult(requestCode, true)
            }
        }
        val mockRoot = mock(
            RootActivity::class.java,
            withSettings().defaultAnswer(RETURNS_DEEP_STUBS)
        )


        with(launchFragment(themeResId = R.style.AppTheme) {
            ProfileFragment(mockRoot, testRegistry,{ MockViewModelFactory(it)} )
        }) {
            onFragment { fragment ->
                // Trigger the ActivityResultLauncher
                val pendingAction = PendingAction.CameraAction(fragment.prepareTempUri())
                fragment.viewModel.updateState { it.copy(pendingAction = pendingAction) }
                fragment.viewModel.startForResult(pendingAction)
                // Verify the result is set
                verify(fragment.viewModel, atLeastOnce()).handleUploadPhoto(any())
            }
        }
    }

    @Test
    fun take_no_result_from_camera() {

        val testRegistry = object : ActivityResultRegistry() {
            override fun <I, O> onLaunch(
                requestCode: Int,
                contract: ActivityResultContract<I, O>,
                input: I,
                options: ActivityOptionsCompat?
            ) {
                dispatchResult(requestCode, false)
            }
        }
        val mockRoot = mock(
            RootActivity::class.java,
            withSettings().defaultAnswer(RETURNS_DEEP_STUBS)
        )


        with(launchFragment(themeResId = R.style.AppTheme) {
            ProfileFragment(mockRoot, testRegistry,{ MockViewModelFactory(it)} )
        }) {
            onFragment { fragment ->
                // Trigger the ActivityResultLauncher
                val pendingAction = PendingAction.CameraAction(fragment.prepareTempUri())
                fragment.viewModel.updateState { it.copy(pendingAction = pendingAction) }
                fragment.viewModel.startForResult(pendingAction)
                // Verify the result is set
                verify(fragment.viewModel, never()).handleUploadPhoto(any())
            }
        }
    }

    @Test
    fun take_result_from_gallery() {
        val expectedUri = Uri.parse("android.resource://ru.skillbranch.skillarticles/" + R.mipmap.ic_launcher)
        val testRegistry = object : ActivityResultRegistry() {
            override fun <I, O> onLaunch(
                requestCode: Int,
                contract: ActivityResultContract<I, O>,
                input: I,
                options: ActivityOptionsCompat?
            ) {
                dispatchResult(requestCode, expectedUri)
            }
        }
        val mockRoot = mock(
            RootActivity::class.java,
            withSettings().defaultAnswer(RETURNS_DEEP_STUBS)
        )

        with(launchFragment(themeResId = R.style.AppTheme) {
            ProfileFragment(mockRoot, testRegistry, { MockViewModelFactory(it)})
        }) {
            onFragment { fragment ->
                // Trigger the ActivityResultLauncher
                val pendingAction = PendingAction.GalleryAction("image/jpeg")
                fragment.viewModel.updateState { it.copy(pendingAction = pendingAction) }
                fragment.viewModel.startForResult(pendingAction)
                // Verify the result is set
                verify(fragment.viewModel, atLeastOnce()).handleUploadPhoto(any())

            }
        }
    }

    @Test
    fun take_no_result_from_gallery() {
        val expectedUri = Uri.parse("android.resource://ru.skillbranch.skillarticles/" + R.mipmap.ic_launcher)
        val testRegistry = object : ActivityResultRegistry() {
            override fun <I, O> onLaunch(
                requestCode: Int,
                contract: ActivityResultContract<I, O>,
                input: I,
                options: ActivityOptionsCompat?
            ) {
                dispatchResult(requestCode, null)
            }
        }
        val mockRoot = mock(
            RootActivity::class.java,
            withSettings().defaultAnswer(RETURNS_DEEP_STUBS)
        )

        with(launchFragment(themeResId = R.style.AppTheme) {
            ProfileFragment(mockRoot, testRegistry, { MockViewModelFactory(it)})
        }) {
            onFragment { fragment ->
                // Trigger the ActivityResultLauncher
                val pendingAction = PendingAction.GalleryAction("image/jpeg")
                fragment.viewModel.updateState { it.copy(pendingAction = pendingAction) }
                fragment.viewModel.startForResult(pendingAction)
                // Verify the result is set
                verify(fragment.viewModel, never()).handleUploadPhoto(any())

            }
        }
    }

    @Test
    fun take_result_from_edit_photo() {
        val expectedUri = Uri.parse("android.resource://ru.skillbranch.skillarticles/" + R.mipmap.ic_launcher)
        val testRegistry = object : ActivityResultRegistry() {
            override fun <I, O> onLaunch(
                requestCode: Int,
                contract: ActivityResultContract<I, O>,
                input: I,
                options: ActivityOptionsCompat?
            ) {
                dispatchResult(requestCode, expectedUri)
            }
        }
        val mockRoot = mock(
            RootActivity::class.java,
            withSettings().defaultAnswer(RETURNS_DEEP_STUBS)
        )

        with(launchFragment(themeResId = R.style.AppTheme) {
            ProfileFragment(mockRoot, testRegistry,{ MockViewModelFactory(it)})
        }) {
            onFragment { fragment ->
                // Trigger the ActivityResultLauncher
                val temp = fragment.prepareTempUri()
                val pendingAction = PendingAction.EditAction(expectedUri to temp)
                fragment.viewModel.updateState { it.copy(pendingAction = pendingAction) }
                fragment.viewModel.startForResult(pendingAction)
                // Verify the result is set
                verify(fragment.viewModel, atLeastOnce()).handleUploadPhoto(any())

            }
        }
    }

    @Test
    fun take_no_result_from_edit_photo() {
        val expectedUri = Uri.parse("android.resource://ru.skillbranch.skillarticles/" + R.mipmap.ic_launcher)
        val testRegistry = object : ActivityResultRegistry() {
            override fun <I, O> onLaunch(
                requestCode: Int,
                contract: ActivityResultContract<I, O>,
                input: I,
                options: ActivityOptionsCompat?
            ) {
                dispatchResult(requestCode, null)
            }
        }
        val mockRoot = mock(
            RootActivity::class.java,
            withSettings().defaultAnswer(RETURNS_DEEP_STUBS)
        )


        with(launchFragment(themeResId = R.style.AppTheme) {
            ProfileFragment(mockRoot, testRegistry,{ MockViewModelFactory(it)})
        }) {
            onFragment { fragment ->
                // Trigger the ActivityResultLauncher
                val temp = fragment.prepareTempUri()
                val pendingAction = PendingAction.EditAction(expectedUri to temp)
                fragment.viewModel.updateState { it.copy(pendingAction = pendingAction) }
                fragment.viewModel.startForResult(pendingAction)
                // Verify the result is set
                verify(fragment.viewModel, never()).handleUploadPhoto(any())

            }
        }
    }
}

class MockViewModelFactory(owner: SavedStateRegistryOwner) : AbstractSavedStateViewModelFactory(owner, null) {
    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        return spy(ProfileViewModel(handle)) as T
    }
}

inline fun <reified T : Any> argumentCaptor(): ArgumentCaptor<T> =
    ArgumentCaptor.forClass(T::class.java)

fun <T> capture(argumentCaptor: ArgumentCaptor<T>): T = argumentCaptor.capture()

private fun <T> any(): T {
    Mockito.any<T>()
    return uninitialized()
}

private fun <T> uninitialized(): T = null as T