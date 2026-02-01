using CartService.Application.DTOs;
using CartService.Application.Services;
using CartService.Domain.Entities;
using Microsoft.AspNetCore.Mvc;

namespace CartService.Controllers;

[ApiController]
[Route("api/cart")]
public class CartController : ControllerBase
{
    private readonly CartManager _cartManager;

    public CartController(CartManager cartManager)
    {
        _cartManager = cartManager;
    }

    // TEMP: replace with JWT later
    private string GetUserId()
    {
        if (Request.Headers.TryGetValue("X-User-Id", out var userId) &&
            !string.IsNullOrWhiteSpace(userId))
        {
            return userId!;
        }

        return "dev-user-001"; // fallback for development
    }

    [HttpGet]
    public async Task<IActionResult> GetCart()
    {
        var userId = GetUserId();
        var cart = await _cartManager.GetOrCreateCartAsync(userId);
        return Ok(cart);
    }

    [HttpPost("items")]
    public async Task<IActionResult> AddItem([FromBody] AddCartItemDto dto)
    {
        var userId = GetUserId();

        var item = new CartItem
        {
            ProductId = dto.ProductId,
            ProductName = dto.ProductName,
            UnitPrice = dto.UnitPrice,
            Quantity = dto.Quantity
        };

        await _cartManager.AddItemAsync(userId, item);
        return Ok();
    }

    [HttpPut("items/{cartItemId:guid}")]
    public async Task<IActionResult> UpdateQuantity(Guid cartItemId, [FromBody] UpdateQuantityDto dto)
    {
        var userId = GetUserId();
        await _cartManager.UpdateQuantityAsync(userId, cartItemId, dto.Quantity);
        return NoContent();
    }

    [HttpDelete("items/{cartItemId:guid}")]
    public async Task<IActionResult> RemoveItem(Guid cartItemId)
    {
        var userId = GetUserId();
        await _cartManager.RemoveItemAsync(userId, cartItemId);
        return NoContent();
    }

    [HttpDelete("clear")]
    public async Task<IActionResult> ClearCart()
    {
        var userId = GetUserId();
        await _cartManager.ClearCartAsync(userId);
        return NoContent();
    }
}
