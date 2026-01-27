const mongoose = require('mongoose');

const reviewSchema = new mongoose.Schema({
  customerId: { type: String },
  rating: { type: Number, min: 1, max: 5 },
  comment: String,
  createdAt: { type: Date, default: Date.now }
});

const productSchema = new mongoose.Schema({
  name: { type: String, required: true },
  rate: { type: Number, required: true },
  description: String,
  tags: [String],
  discount: { type: Number, default: 0 },
  stockQuantity: { type: Number, default: 0 },
  imageUrl: [String],
  gender: { type: String },
  merchantId: { type: String },
  createdAt: { type: Date, default: Date.now },
  sizes: [String],
  colors: [String],
  rating: { type: Number, default: 0 },
  review: { type: Number, default: 0 },
  customersReviews: [reviewSchema],
  createdAt: { type: Date, default: Date.now }
});

module.exports = mongoose.model("Product", productSchema);
