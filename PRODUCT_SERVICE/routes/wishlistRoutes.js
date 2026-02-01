const express = require("express");
const { addToWishlist, getWishlist } =require( "../controllers/wishlistController.js");


const router = express.Router();

router.post("/", addToWishlist);
router.get("/:customerId", getWishlist);

module.exports = router;
