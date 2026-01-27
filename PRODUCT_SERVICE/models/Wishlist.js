const mongoose = require('mongoose');


const wishlistSchema = new mongoose.Schema({
  customerId: { type: String, required: true },
  products: [{ type: mongoose.Schema.Types.ObjectId, ref: "Product" }],
  addedAt: { type: Date, default: Date.now }
});

module.exports = mongoose.model("Wishlist", wishlistSchema);
