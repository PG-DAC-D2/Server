const express = require("express");
const dotenv = require("dotenv");
const cors = require("cors");
const connectDB = require("./config/db.js");

const productRoutes = require("./routes/productRoutes.js");
const wishlistRoutes = require("./routes/wishlistRoutes.js");
const addressRoutes = require("./routes/addressRoutes.js");

dotenv.config();
connectDB();

const app = express();

// CORS configuration - only allow from API Gateway
const corsOptions = {
  origin: ["http://localhost:8080"],
  credentials: true,
  methods: ["GET", "POST", "PUT", "DELETE", "OPTIONS"],
  allowedHeaders: ["Content-Type", "Authorization", "X-User-Id"],
  // allow custom user id header used by frontend
  exposedHeaders: ["X-User-Id"],
};

app.use(cors(corsOptions));
app.use(express.json());

app.use("/products", productRoutes);
app.use("/wishlist", wishlistRoutes);
app.use("/addresses", addressRoutes);
// also expose under /api/addresses so frontend calls with /api prefix work
app.use("/api/addresses", addressRoutes);

// test api
app.get("/test", (req, res) => {
  res.json({ message: "API is working fine!" });
});

const PORT = process.env.PORT || 5000;
app.listen(PORT, "0.0.0.0", () =>
  console.log(`Server running on port ${PORT}`),
);
