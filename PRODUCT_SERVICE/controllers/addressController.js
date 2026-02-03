const Address = require('../models/Address.js');

// Add a single address for a user (create doc if missing)
const addAddress = async (req, res) => {
  try {
    const { userId, address } = req.body;
    if (!userId || !address) return res.status(400).json({ message: 'userId and address required' });

    let doc = await Address.findOne({ userId });
    if (!doc) {
      doc = await Address.create({ userId, addresses: [address] });
    } else {
      doc.addresses.push(address);
      await doc.save();
    }
    res.status(201).json(doc);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
};

// Get addresses for a userId
const getAddressesByUser = async (req, res) => {
  try {
    const { userId } = req.params;
    const doc = await Address.findOne({ userId });
    if (!doc) return res.status(404).json({ message: 'No addresses found for this user' });
    res.json(doc);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
};

// Update an existing address for a user
const updateAddress = async (req, res) => {
  try {
    const { userId, addressId } = req.params;
    const { address } = req.body;
    if (!userId || !addressId || !address) return res.status(400).json({ message: 'userId, addressId and address required' });

    const doc = await Address.findOne({ userId });
    if (!doc) return res.status(404).json({ message: 'User addresses not found' });

    // find index of subdocument (support both Mongoose subdocs and plain objects)
    const idx = doc.addresses.findIndex(a => String(a._id) === String(addressId));
    if (idx === -1) return res.status(404).json({ message: 'Address not found' });

    // update allowed fields on the found address object
    Object.keys(address).forEach((k) => {
      doc.addresses[idx][k] = address[k];
    });

    await doc.save();
    res.json(doc);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
};

// Delete an address by id for a user
const deleteAddress = async (req, res) => {
  try {
    const { userId, addressId } = req.params;
    if (!userId || !addressId) return res.status(400).json({ message: 'userId and addressId required' });

    const doc = await Address.findOne({ userId });
    if (!doc) return res.status(404).json({ message: 'User addresses not found' });

    // remove by filtering to avoid relying on subdocument remove()
    const before = doc.addresses.length;
    doc.addresses = doc.addresses.filter(a => String(a._id) !== String(addressId));
    if (doc.addresses.length === before) return res.status(404).json({ message: 'Address not found' });
    await doc.save();
    res.json(doc);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
};

module.exports = { addAddress, getAddressesByUser, updateAddress, deleteAddress };

