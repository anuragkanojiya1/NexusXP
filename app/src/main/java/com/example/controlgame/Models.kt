package com.example.controlgame

//object Models {
//
//    val models = listOf(
//        Model(id = 0, modelPath = "models/audi.glb", modelName = ""),
//        Model(id = 1, modelPath = "models/damaged_helmet.glb", modelName = ""),
//        Model(id = 3, modelPath = "models/telimia__original_work.glb", modelName = "")
//    )
//
//}

val models = listOf(
    ModelItem("0", "10", "100", "Audi", "3D model", R.drawable.audi, "models/audi.glb"),
    ModelItem("1", "30", "200", "Car", "3D model", R.drawable.redcar, "models/car1.glb"),
    ModelItem("2", "70", "250", "BMW", "3D model", R.drawable.bmw, "models/bmw.glb"),
    ModelItem("3", "100", "300", "The Aegis Dominator", "3D model", R.drawable.tank, "models/tank.glb"),
    ModelItem("4", "3000", "1000", "Firefly", "3D model", R.drawable.firefly, "models/advanced_vehicle.glb"),
)

data class ModelItem(
    val id: String,
    val price: String,
    val unlockScore: String,
    val name: String,
    val type: String,
    val imageUrl: Int,
    val modelPath: String
)

data class Model(val id: Int, val modelPath: String, val modelName: String)
