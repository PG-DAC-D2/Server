const Wishlist =require( "../models/Wishlist.js");

const addToWishlist = async (req, res) => {
  const { customerId, productId } = req.body;
  let wishlist = await Wishlist.findOne({ customerId });

  if (!wishlist) wishlist = new Wishlist({ customerId, products: [productId] });
  else if (!wishlist.products.includes(productId)) wishlist.products.push(productId);

  await wishlist.save();
  res.json(wishlist);
};

const getWishlist = async (req, res) => {
  const wishlist = await Wishlist.findOne({ customerId: req.params.customerId }).populate("products");
  res.json(wishlist || { products: [] });
};

module.exports = {
  addToWishlist,
  getWishlist
};
