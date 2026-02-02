using Microsoft.AspNetCore.Mvc;
using OrderService.Application.DTOs;
using OrderService.Application.Services;
using OrderService.Infrastructure.Persistence;
using Microsoft.EntityFrameworkCore;

namespace OrderService.Controllers;

[ApiController]
[Route("orders")]
public class OrdersController : ControllerBase
{
    private readonly OrderManager _orderManager;
    private readonly OrderDbContext _db;

    public OrdersController(OrderManager orderManager, OrderDbContext db)
    {
        _orderManager = orderManager;
        _db = db;
    }

    private string GetUserId()
    {
        if (Request.Headers.TryGetValue("X-User-Id", out var userId) &&
            !string.IsNullOrWhiteSpace(userId))
        {
            return userId!;
        }

        return "dev-user-001";
    }

    [HttpPost]
    public async Task<IActionResult> CreateOrder(CreateOrderDto dto)
    {
        var userId = GetUserId();

        var orderId = await _orderManager.CreateOrderAsync(userId, dto);

        return Ok(new { orderId });
    }

    [HttpGet("{orderId:guid}")]
    public async Task<IActionResult> GetOrder(Guid orderId)
    {
        var order = await _db.Orders
            .Include(o => o.Items)
            .FirstOrDefaultAsync(o => o.OrderId == orderId);

        if (order == null)
            return NotFound();

        return Ok(order);
    }

    [HttpGet("my")]
    public async Task<IActionResult> GetMyOrders()
    {
        var userId = GetUserId();

        var orders = await _db.Orders
            .Where(o => o.UserId == userId)
            .Include(o => o.Items)
            .ToListAsync();

        return Ok(orders);
    }
}
