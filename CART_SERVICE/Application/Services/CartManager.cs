using CartService.Domain.Entities;
using CartService.Infrastructure.Persistence;
using Microsoft.EntityFrameworkCore;

namespace CartService.Application.Services;

public class CartManager
{
    private readonly CartDbContext _db;

    public CartManager(CartDbContext db)
    {
        _db = db;
    }

    public async Task<ShoppingCart> GetOrCreateCartAsync(string userId)
{
    var cart = await _db.Carts
        .Include(c => c.Items)
        .FirstOrDefaultAsync(c => c.UserId == userId);

    if (cart == null)
    {
        cart = new ShoppingCart
        {
            CartId = Guid.NewGuid(),
            UserId = userId,
            CreatedAt = DateTime.UtcNow,
            Items = new List<CartItem>()
        };

        _db.Carts.Add(cart);
        await _db.SaveChangesAsync();
    }

    return cart;
}


    public async Task<ShoppingCart> GetCartAsync(string userId)
    {
        var cart = await _db.Carts
            .Include(c => c.Items)
            .FirstOrDefaultAsync(c => c.UserId == userId);

        if (cart == null)
            throw new KeyNotFoundException("Cart not found.");

        return cart;
    }

    // CartManager.cs
public async Task<ShoppingCart> AddItemAsync(string userId, CartItem item)
{
    var cart = await GetOrCreateCartAsync(userId);

    var existingItem = await _db.CartItems
        .FirstOrDefaultAsync(i =>
            i.CartId == cart.CartId &&
            i.ProductId == item.ProductId);

    if (existingItem != null)
    {
        existingItem.Quantity += item.Quantity;
    }
    else
    {
        item.CartId = cart.CartId;
        // Ensure the ID is generated if not handled by DB identity
        if (item.CartItemId == Guid.Empty) item.CartItemId = Guid.NewGuid(); 
        await _db.CartItems.AddAsync(item);
    }

    await _db.SaveChangesAsync();
    
    // Return the full cart with the new database-generated IDs
    return await GetOrCreateCartAsync(userId); 
}


    public async Task UpdateQuantityAsync(string userId, Guid cartItemId, int quantity)
    {
        if (quantity <= 0)
            throw new ArgumentException("Quantity must be greater than zero.");

        var cart = await GetOrCreateCartAsync(userId);

        var item = cart.Items.FirstOrDefault(i => i.CartItemId == cartItemId);

        if (item == null)
            throw new KeyNotFoundException("Cart item not found.");

        item.Quantity = quantity;
        await _db.SaveChangesAsync();
    }

    public async Task RemoveItemAsync(string userId, Guid cartItemId)
    {
        var cart = await GetOrCreateCartAsync(userId);

        var item = cart.Items.FirstOrDefault(i => i.CartItemId == cartItemId);

        if (item == null)
            throw new KeyNotFoundException("Cart item not found.");

        _db.CartItems.Remove(item);
        await _db.SaveChangesAsync();
    }

    public async Task ClearCartAsync(string userId)
    {
        var cart = await GetOrCreateCartAsync(userId);

        _db.CartItems.RemoveRange(cart.Items);
        await _db.SaveChangesAsync();
    }
}
