const express = require("express");
const { createProduct, getProducts, getProductById, updateProduct, deleteProduct, addReview } = require( "../controllers/productController.js");
const router = express.Router();

router.post("/", createProduct);
router.get("/", getProducts);
router.get("/:id", getProductById);
router.put("/:id", updateProduct);
router.delete("/:id", deleteProduct);
router.post("/:id/review", addReview);

module.exports = router;
