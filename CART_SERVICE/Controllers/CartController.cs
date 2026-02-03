using CartService.Application.DTOs;
using CartService.Application.Services;
using CartService.Domain.Entities;
using Microsoft.AspNetCore.Mvc;
using System.IdentityModel.Tokens.Jwt;
using System.Linq;
using System.Security.Claims;

namespace CartService.Controllers;

[ApiController]
[Route("cart")]
public class CartController : ControllerBase
{
    private readonly CartManager _cartManager;

    public CartController(CartManager cartManager)
    {
        _cartManager = cartManager;
    }

    private string? GetUserId()
    {
        try
        {
            // 1. PRIORITY: Check the custom header injected by the API Gateway
            if (Request.Headers.TryGetValue("X-User-Id", out var userIdHeader) && 
                !string.IsNullOrWhiteSpace(userIdHeader))
            {
                return userIdHeader.ToString();
            }

            // 2. FALLBACK: Check authenticated principal (if UseAuthentication/UseAuthorization is active)
            var user = HttpContext?.User;
            if (user?.Identity != null && user.Identity.IsAuthenticated)
            {
                var userId = user.FindFirst("user_id")?.Value
                             ?? user.FindFirst("sub")?.Value
                             ?? user.FindFirst(ClaimTypes.NameIdentifier)?.Value;
                if (!string.IsNullOrEmpty(userId))
                    return userId;
            }

            // 3. FINAL FALLBACK: Manual JWT parse from Authorization header
            if (Request.Headers.TryGetValue("Authorization", out var authHeader) &&
                !string.IsNullOrWhiteSpace(authHeader))
            {
                var token = authHeader.ToString().Replace("Bearer ", "", StringComparison.OrdinalIgnoreCase).Trim();
                var jwtHandler = new JwtSecurityTokenHandler();
                if (jwtHandler.CanReadToken(token))
                {
                    var jwtToken = jwtHandler.ReadJwtToken(token);
                    return jwtToken.Claims.FirstOrDefault(c => 
                        c.Type == "user_id" || 
                        c.Type == "sub" || 
                        c.Type == "nameid")?.Value;
                }
            }
        }
        catch
        {
            // Fail silently and return null for unauthenticated status
        }

        return null;
    } 

    [HttpGet]
    public async Task<IActionResult> GetCart()
    {
        var userId = Request.Headers["X-User-Id"].FirstOrDefault();
        if (userId == null)
            return Unauthorized(new { message = "User identification missing." });

        var cart = await _cartManager.GetOrCreateCartAsync(userId);
        return Ok(cart);
    }

    [HttpPost("items")]
    public async Task<IActionResult> AddItem([FromBody] AddCartItemDto dto)
    {
        var userId = GetUserId();
        if (userId == null)
            return Unauthorized(new { message = "User identification missing." });

        var item = new CartItem
        {
            ProductId = dto.ProductId,
            ProductName = dto.ProductName,
            UnitPrice = dto.UnitPrice,
            Quantity = dto.Quantity
        };

        await _cartManager.AddItemAsync(userId, item);
        return Ok(new { message = "Item added to cart." });
    }

    [HttpPut("items/{cartItemId:guid}")]
    public async Task<IActionResult> UpdateQuantity(Guid cartItemId, [FromBody] UpdateQuantityDto dto)
    {
        var userId = GetUserId();
        if (userId == null)
            return Unauthorized();

        await _cartManager.UpdateQuantityAsync(userId, cartItemId, dto.Quantity);
        return NoContent();
    }

    [HttpDelete("items/{cartItemId:guid}")]
    public async Task<IActionResult> RemoveItem(Guid cartItemId)
    {
        var userId = GetUserId();
        if (userId == null)
            return Unauthorized();

        await _cartManager.RemoveItemAsync(userId, cartItemId);
        return NoContent();
    }

    [HttpDelete("clear")]
    public async Task<IActionResult> ClearCart()
    {
        var userId = GetUserId();
        if (userId == null)
            return Unauthorized();

        await _cartManager.ClearCartAsync(userId);
        return NoContent();
    }
}