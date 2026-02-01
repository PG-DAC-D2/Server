using System.ComponentModel.DataAnnotations;

namespace OrderService.Domain.Entities;

public class Order
{
    [Key]
    public Guid OrderId { get; set; }

    public string UserId { get; set; } = default!;

    public string Status { get; set; } = "PendingPayment";

    public decimal TotalAmount { get; set; }

    public string? PaymentReference { get; set; }

    public string ShippingAddressJson { get; set; } = default!;
    public string BillingAddressJson { get; set; } = default!;

    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;

    public List<OrderItem> Items { get; set; } = new();
}
