package ru.skillbranch.skillarticles.viewmodels.article

import androidx.lifecycle.LiveData
import ru.skillbranch.skillarticles.data.models.ArticleData
import ru.skillbranch.skillarticles.data.models.ArticlePersonalInfo
import ru.skillbranch.skillarticles.data.repositories.MarkdownElement

interface IArticleViewModel {
    /**
     * Получение полной информации о статье из сети
     * (или базы данных если она сохранена, наличие статьи в базе не надо реализовывать в данном уроке)
     */
    fun getArticleContent(): LiveData<List<MarkdownElement>?>

    /**
     * Получение краткой информации о статье из базы данных
     */
    fun getArticleData(): LiveData<ArticleData?>

    /**
     * Получение пользовательской информации о статье из базы данных
     */
    fun getArticlePersonalInfo(): LiveData<ArticlePersonalInfo?>

    /**
     * Получение настроек приложения
     */
    fun handleNightMode()


    /**
     * Обработка нажатия на btn_text_up (увеличение шрифта текста)
     * необходимо увеличить шрифт до значения 18
     */
    fun handleUpText()

    /**
     * Обработка нажатия на btn_text_down (стандартный размер шрифта)
     * необходимо установить размер шрифта по умолчанию 14
     */
    fun handleDownText()

    /**
     * добавление/удалние статьи в закладки, обрабока нажатия на кнопку btn_bookmark
     * необходимо отобразить сообщение пользователю "Add to bookmarks" или "Remove from bookmarks"
     * в соответствии с текущим состоянием
     */
    fun handleBookmark()

    /**
     * добавление/удалние статьи в понравившееся, обрабока нажатия на кнопку btn_like
     * необходимо отобразить сообщение пользователю (Notify.ActionMessage) "Mark is liked" или
     * "Don`t like it anymore"  в соответствии с текущим состоянием.
     * если пользователь убрал Like необходимо добавить  actionLabel в снекбар
     * "No, still like it" при нажатиии на который состояние вернется к isLike = true
     */
    fun handleLike()

    /**
     * поделиться статьей, обрабока нажатия на кнопку btn_share
     * необходимо отобразить сообщение с ошибкой пользователю (Notify.ErrorMessage) "Share is not implemented"
     * и текстом errLabel "OK"
     */
    fun handleShare()

    /**
     * обрабока нажатия на кнопку btn_settings
     * необходимо отобразить или скрыть меню в соответствии с текущим состоянием
     */
    fun handleToggleMenu()

    /**
     * обрабока перехода в режим поиска searchView
     * при нажатии на пункту меню тулбара необходимо отобразить searchView и сохранить состояние при
     * изменении конфигурации (пересоздании активити)
     */
    fun handleSearchMode(isSearch: Boolean)

    /**
     * обрабока поискового запроса, необходимо сохранить поисковый запрос и отображать его в
     * searchView при изменении конфигурации (пересоздании активити)
     */
    fun handleSearch(query: String?)

    /**
     * обрабока нажатия на btn_result_up ,необходимо перенести фокус на предидущее поисковое вхождение
     */
    fun handleUpResult()

    /**
     * обрабока нажатия на btn_result_down ,необходимо перенести фокус на следующее поисковое вхождение
     */
    fun handleDownResult()

    /**
     * обрабока нажатия на iv_copy в MarkdownCodeView, необходимо скопировать код из MarkdownCodeView в буфер обмена
     **/
    fun handleCopyCode()

    /**
     * обрабока отправки комментария, если пользователь не авторизован отобразить экран авторизации
     **/
    fun handleSendComment(comment: String?)
}