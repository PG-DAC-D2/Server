const express = require('express');
const router = express.Router();
const { addAddress, getAddressesByUser, updateAddress, deleteAddress } = require('../controllers/addressController.js');

router.post('/', addAddress);
router.get('/:userId', getAddressesByUser);
router.put('/:userId/:addressId', updateAddress);
router.delete('/:userId/:addressId', deleteAddress);

module.exports = router;
