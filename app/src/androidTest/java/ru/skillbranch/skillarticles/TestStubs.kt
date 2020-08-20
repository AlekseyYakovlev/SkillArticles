package ru.skillbranch.skillarticles

import ru.skillbranch.skillarticles.data.local.entities.Article
import ru.skillbranch.skillarticles.data.local.entities.ArticleCounts
import ru.skillbranch.skillarticles.data.local.entities.Author
import java.util.*

object TestStubs {
    val articlesInitialRes: String = """
    [
        {
                "data": {
                    "id": "5f27d6cf83218a001d059af0",
                    "date": 1596446410829,
                    "author": {
                        "id": "5f27d6cb83218a001d05965c",
                        "avatar": "https://miro.medium.com/fit/c/96/96/2*_ufZYLoBvvfqNk_m9CuLnw.png",
                        "name": "inVerta"
                    },
                    "title": "Flutter vs Native vs React-Native: Examining performance",
                    "description": "Today some of the most popular solutions to build mobile apps are native or cross-platform approaches using React Native or Flutter",
                    "poster": "https://miro.medium.com/max/1400/1*_5uHflEhilD6S0cvr6wzPw.jpeg",
                    "category": {
                        "id": "5f27d6cb83218a001d05965d",
                        "icon": "https://skill-branch.ru/img/mail/bot/flutter-icon.png",
                        "title": "Flutter"
                    },
                    "tags": [
                        "#Programming",
                        "#Development",
                        "#iOS",
                        "#Android",
                        "#Flutter",
                        "#Native App"
                    ],
                    "updatedAt": 1596446415316
                },
                "counts": {
                    "articleId": "5f27d6cf83218a001d059af0",
                    "likes": 0,
                    "comments": 6,
                    "readDuration": 3,
                    "updatedAt": 1597424037503
                },
                "isActive": true,
                "updatedAt": 1596446415316
            }
    ]
    """.trimIndent()

    val articlesAfterRes: String = """
    [
        {
            "data": {
                "id": "5f27d6cf83218a001d059aed",
                "date": 1596360010829,
                "author": {
                    "id": "5f27d6cb83218a001d059658",
                    "avatar": "https://miro.medium.com/fit/c/96/96/2*FWVYiIti311X3IES3TTzgA.jpeg",
                    "name": "Sunitha Balasubramanian"
                },
                "title": "Enums in Swift",
                "description": "Create your Own Types",
                "poster": "https://miro.medium.com/max/1400/0*gnqSTcmtBDaaUVnk",
                "category": {
                    "id": "5f27d6cb83218a001d059654",
                    "icon": "https://skill-branch.ru/img/mail/bot/ios-icon.png",
                    "title": "iOS"
                },
                "tags": [
                    "#iOS",
                    "#Swift",
                    "#Enumeration",
                    "#App Development",
                    "#Swift Programming"
                ],
                "updatedAt": 1596446415310
            },
            "counts": {
                "articleId": "5f27d6cf83218a001d059aed",
                "likes": 0,
                "comments": 4,
                "readDuration": 3,
                "updatedAt": 1596897074021
            },
            "isActive": true,
            "updatedAt": 1596446415310
        },
        {
            "data": {
                "id": "5f27d6cf83218a001d059aea",
                "date": 1596273610829,
                "author": {
                    "id": "5f27d6cb83218a001d059653",
                    "avatar": "https://miro.medium.com/fit/c/96/96/1*DBp49NznjtATlDUu88TANQ.png",
                    "name": "Zafar Ivaev"
                },
                "title": "How to Use NumberFormatter in Swift?",
                "description": "Represent numerical values the way you want",
                "poster": "https://miro.medium.com/max/1400/0*w3vX-8aor1AQmd7n",
                "category": {
                    "id": "5f27d6cb83218a001d059654",
                    "icon": "https://skill-branch.ru/img/mail/bot/ios-icon.png",
                    "title": "iOS"
                },
                "tags": [
                    "#Swift",
                    "#iOS",
                    "#Mobile",
                    "#Programming"
                ],
                "updatedAt": 1596446415304
            },
            "counts": {
                "articleId": "5f27d6cf83218a001d059aea",
                "likes": 0,
                "comments": 0,
                "readDuration": 3,
                "updatedAt": 1596446415303
            },
            "isActive": true,
            "updatedAt": 1596446415304
        }
    ]
    """.trimIndent()

    val articlesBeforeRes: String = """
        [
            {
                "data": {
                    "id": "5f27d6cf83218a001d059af0",
                    "date": 1596446410829,
                    "author": {
                        "id": "5f27d6cb83218a001d05965c",
                        "avatar": "https://miro.medium.com/fit/c/96/96/2*_ufZYLoBvvfqNk_m9CuLnw.png",
                        "name": "inVerta"
                    },
                    "title": "Flutter vs Native vs React-Native: Examining performance",
                    "description": "Today some of the most popular solutions to build mobile apps are native or cross-platform approaches using React Native or Flutter",
                    "poster": "https://miro.medium.com/max/1400/1*_5uHflEhilD6S0cvr6wzPw.jpeg",
                    "category": {
                        "id": "5f27d6cb83218a001d05965d",
                        "icon": "https://skill-branch.ru/img/mail/bot/flutter-icon.png",
                        "title": "Flutter"
                    },
                    "tags": [
                        "#Programming",
                        "#Development",
                        "#iOS",
                        "#Android",
                        "#Flutter",
                        "#Native App"
                    ],
                    "updatedAt": 1596446415316
                },
                "counts": {
                    "articleId": "5f27d6cf83218a001d059af0",
                    "likes": 0,
                    "comments": 6,
                    "readDuration": 3,
                    "updatedAt": 1597424037503
                },
                "isActive": true,
                "updatedAt": 1596446415316
            },
            {
                "data": {
                    "id": "5f27d6cf83218a001d059aed",
                    "date": 1596360010829,
                    "author": {
                        "id": "5f27d6cb83218a001d059658",
                        "avatar": "https://miro.medium.com/fit/c/96/96/2*FWVYiIti311X3IES3TTzgA.jpeg",
                        "name": "Sunitha Balasubramanian"
                    },
                    "title": "Enums in Swift",
                    "description": "Create your Own Types",
                    "poster": "https://miro.medium.com/max/1400/0*gnqSTcmtBDaaUVnk",
                    "category": {
                        "id": "5f27d6cb83218a001d059654",
                        "icon": "https://skill-branch.ru/img/mail/bot/ios-icon.png",
                        "title": "iOS"
                    },
                    "tags": [
                        "#iOS",
                        "#Swift",
                        "#Enumeration",
                        "#App Development",
                        "#Swift Programming"
                    ],
                    "updatedAt": 1596446415310
                },
                "counts": {
                    "articleId": "5f27d6cf83218a001d059aed",
                    "likes": 0,
                    "comments": 4,
                    "readDuration": 3,
                    "updatedAt": 1596897074021
                },
                "isActive": true,
                "updatedAt": 1596446415310
            }
        ]
    """.trimIndent()

    val stubArticle = Article(
        id = "5f27d6cf83218a001d059af0",
        title = "test",
        description = "test",
        author = Author(
            userId = "test",
            avatar = null,
            name = "test"
        ),
        categoryId = "test",
        poster = "test",
        date = Date(),
        updatedAt = Date()
    )

    val stubArticleCounts = ArticleCounts(
        updatedAt = Date(),
        likes = 0,
        comments = 6,
        readDuration = 3,
        articleId = "5f27d6cf83218a001d059af0"
    )

    val articleContentRes: String = """
        {
            "updatedAt": 1596446415316,
            "content": "Today some of the most popular solutions to build mobile apps are native or cross-platform approaches using React Native or Flutter. While native development is positioned as AAA technical solution, it has some disadvantages that create market space for cross-platform apps to come in. In general, native development requires more effort from the development team to accomplish the project but it gives full control over tricky technical stuff under the hood. On the other hand, if you choose cross-platform, it can significantly speed up the development process due to a common code base, make project support easier and reduce expenses for development.\nOne more advantage of native over cross-platform development is performance. In the technical world, you can encounter “cross-platform apps are slow” stereotypes. We decided to test if it’s true and to what extend cross-platform apps are slower than native.\n***\n### There are different types of performance, some of them are:\n* Interacting with phone API (accessing photos, file system, getting GPS location and so on).\n* Rendering speed (animation smoothness, frames per second while UI is changed or some UI effects that take place in time).\n* Business logic (the speed of mathematical calculations and memory manipulations. This type of performance is most important for the apps with complex business logic).\nIn this article, we share the results of performance tests showing mathematical calculations of number Pi implemented in native and cross-platform approaches.\n***\n### CPU-intensive test (Gauss–Legendre algorithm) for iOS\n![Memory-intensive test (Gauss–Legendre algorithm) for iOS](https://miro.medium.com/max/1400/1*Ey1f_IcZiwM75XGKY83nVw.jpeg \"Memory-intensive test (Gauss–Legendre algorithm) for iOS\")\n#### iOS\n* Objective-C is the best programming language for iOS development. Swift is 1.7 times slower compared to Objective C.\n* Surprise: Flutter is a bit faster than Swift (on 15%).\n* React Native is 20 times slower than Objective C.\n### CPU-intensive test (Borwein algorithm) for iOS\n![CPU-intensive test (Borwein algorithm) for iOS](https://miro.medium.com/max/1400/1*wHGAVXahLFWFPRrzmYN6fw.jpeg \"CPU-intensive test (Borwein algorithm) for iOS\")\n#### iOS\n* Objective C is the best option for iOS app development. Swift is 1.9 times slower compared to Objective-C.\n* Flutter is 5 times slower than Swift.\n* React Native version is more than 15 times slower than the Swift version.\n### CPU-intensive test (Gauss–Legendre algorithm) for Android\n![Memory-intensive test (Gauss–Legendre algorithm) for Android](https://miro.medium.com/max/1400/1*C5bM9KdtdjHpftFxgBn-UA.jpeg \"Memory-intensive test (Gauss–Legendre algorithm) for Android\")\n#### Android\n* Java and Kotlin have similar performance indications and are the best options for Android development.\n* Flutter is approximately 20% slower than native.\n* React Native is around 15 times slower than native.\n### CPU-intensive test (Borwein algorithm) for Android\n![CPU-intensive test (Borwein algorithm) for Android](https://miro.medium.com/max/1400/1*zf1pnAPXytzFvlqj_aDThA.jpeg \"CPU-intensive test (Borwein algorithm) for Android\")\n#### Android\n* Java and Kotlin have similar performance indications and are the best options for Android development.\n* Native is 2 times faster then Flutter.\n* React native is around 6 times slower than native.\n***\n### Technical details:\n* All tests have been done on real physical devices (iPhone 6s IOS 13.2.3 and Xiaomi Redmi Note 5 running under Android 9.0);\n* We measured performance on release builds. In some cases, debug builds can be significantly slower than the release builds.\n* All tests were run several times and the average result was calculated.\n* Gauss–Legendre & Borwein algorithms of calculating Pi numbers were used. The Pi number has been calculated 100 times with 10 million digits precision.\n* Gauss–Legendre is a more memory-intensive algorithm in comparison with Borwein, but Borwein is more CPU-intensive.\n* [Source code](https://github.com/nazarcybulskij/Mobile_Bechmarks_)\n***\n### Key takeaways\n* In summary, not all cross-platform apps are slow. What’s more than that, Flutter apps have higher performance than Swift apps.\n* Objective C and Flutter will be a wise choice if you want to develop a super-fast iOS app.\n* For the apps with high load calculations Flutter is a good option for both, Android and iOS app development.\nPlease let inVerita know if you struggle with picking a mobile tool for development, always happy to help.",
            "source": "https://medium.com/swlh/flutter-vs-native-vs-react-native-examining-performance-31338f081980",
            "shareLink": "https://medium.com/swlh/flutter-vs-native-vs-react-native-examining-performance-31338f081980",
            "articleId": "5f27d6cf83218a001d059af0"
        }
    """.trimIndent()

    val articleCountsRes: String = """
        {
            "updatedAt": 1597502797533,
            "likes": 0,
            "comments": 6,
            "readDuration": 3,
            "articleId": "5f27d6cf83218a001d059af0"
        }
    """.trimIndent()

    val loginRes: String = """
    {
        "user": {
            "updatedAt": 1596535205494,
            "id": "5f27dad7966af6001c228d3b",
            "avatar": "https://skill-articles.skill-branch.ru/uploads/profile/asR28h4cCf.jpg",
            "name": "Михаил Макеев",
            "about": "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.",
            "rating": 200,
            "respect": 10
        },
        "refreshToken": "test_refresh_token",
        "accessToken": "test_acess_token"
    }
        """

    val messageRes = """
        {
            "message": {
                "id": "5f37fd1ebd6351001c26a71f",
                "author": {
                    "id": "5f27dad7966af6001c228d3b",
                    "name": "Михаил Макеев",
                    "avatar": "https://skill-articles.skill-branch.ru/uploads/profile/asR28h4cCf.jpg",
                    "rating": 200,
                    "respect": 10
                },
                "message": "test",
                "date": 1597504798474,
                "slug": "a71e/a71f/",
                "answerTo": "Михаил Макеев"
            },
            "messageCount": 9
        }
    """.trimIndent()
}