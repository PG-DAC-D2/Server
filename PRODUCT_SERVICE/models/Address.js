const mongoose = require('mongoose');

const addressSubSchema = new mongoose.Schema({
  houseAndFloor: { type: String },
  buildingBlock: { type: String },
  landmark: { type: String },
  label: { type: String, enum: ["Home", "Work", "Other"] },
  receiverName: { type: String },
  receiverPhone: { type: String },
  createdAt: { type: Date, default: Date.now }
});

const addressSchema = new mongoose.Schema({
  userId: { type: String, required: true, unique: true },
  addresses: [addressSubSchema],
  createdAt: { type: Date, default: Date.now }
});

module.exports = mongoose.model('Address', addressSchema);
