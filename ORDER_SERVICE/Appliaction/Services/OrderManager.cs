using OrderService.Application.DTOs;
using OrderService.Domain.Entities;
using OrderService.Infrastructure.Persistence;

namespace OrderService.Application.Services;

public class OrderManager
{
    private readonly OrderDbContext _db;

    public OrderManager(OrderDbContext db)
    {
        _db = db;
    }

    public async Task<Guid> CreateOrderAsync(string userId, CreateOrderDto dto)
    {
        decimal total = dto.Items.Sum(i => i.UnitPrice * i.Quantity);

        var order = new Order
        {
            UserId = userId,
            TotalAmount = total,
            ShippingAddressJson = dto.ShippingAddressJson,
            BillingAddressJson = dto.BillingAddressJson,
            Status = "PendingPayment"
        };

        foreach (var item in dto.Items)
        {
            order.Items.Add(new OrderItem
            {
                ProductId = item.ProductId,
                ProductName = item.ProductName,
                UnitPrice = item.UnitPrice,
                Quantity = item.Quantity
            });
        }

        _db.Orders.Add(order);
        await _db.SaveChangesAsync();

        // TODO: publish OrderCreated event (Kafka)
        return order.OrderId;
    }
}
