const express = require("express");
const dotenv = require("dotenv");
const cors = require("cors");
const connectDB = require("./config/db.js");

const productRoutes = require("./routes/productRoutes.js");
const wishlistRoutes = require("./routes/wishlistRoutes.js");


dotenv.config();
connectDB();

const app = express();
app.use(cors());
app.use(express.json());

app.use("/api/products", productRoutes);
app.use("/api/wishlist", wishlistRoutes);

// test api
app.get("/api/test", (req, res) => {
  res.json({ message: "API is working fine!" });
});

const PORT = process.env.PORT || 5000;
app.listen(PORT,"0.0.0.0", () => console.log(`Server running on port ${PORT}`));




