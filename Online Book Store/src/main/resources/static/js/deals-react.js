(function () {
  // Optional enhancement: show deals as a small React component
  // This is a progressive enhancement; the core site works without JS.
  var rootEl = document.getElementById("deals-react-root");
  if (!rootEl || !window.React || !window.ReactDOM) return;

  function DealsBanner(props) {
    var deals = props.deals || [];
    if (!deals.length) return null;

    return React.createElement(
      "div",
      { className: "card", style: { marginTop: "12px", padding: "12px" } },
      React.createElement("div", { className: "row", style: { justifyContent: "space-between" } },
        React.createElement("div", null,
          React.createElement("div", { className: "title" }, "Today’s Deals (15–20%)"),
          React.createElement("div", { className: "muted small" }, "Login to purchase. Deals apply automatically.")
        ),
        React.createElement("a", { className: "btn", href: "/books/deals" }, "View all deals")
      ),
      React.createElement(
        "div",
        { className: "row gap", style: { marginTop: "10px", flexWrap: "wrap" } },
        deals.slice(0, 4).map(function (d) {
          return React.createElement(
            "a",
            { key: d.id, href: "/books/" + d.id, className: "badge", style: { cursor: "pointer" } },
            d.title + " (-" + d.discountPercent + "%)"
          );
        })
      )
    );
  }

  // Thymeleaf serializes objects poorly for data attributes; simplest: fetch via public API
  fetch("/api/books?deals=true")
    .then(function (r) { return r.json(); })
    .then(function (deals) {
      var root = ReactDOM.createRoot(rootEl);
      root.render(React.createElement(DealsBanner, { deals: deals }));
    })
    .catch(function () {});
})();
