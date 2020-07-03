package com.test.postersapp.domain.model

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

//@Entity
//class Poster (
//    val id: String,
//    val poster: String,
//    val year: Int
//) {
//    @Id
//    var storeId:Long = 0L
//}
@Entity
class Poster {
    @Id
    var storeId: Long = 0L
    var id: String = ""
    var poster: String = ""
    var year: Int = 0

    constructor()

    constructor(
        id: String,
        poster: String,
        year: Int
    ) {
        this.id = id
        this.poster = poster
        this.year = year
    }

}


