using System;
using System.IO;
using System.Linq;
using NUnit.Framework;
using Xamarin.UITest;
using Xamarin.UITest.Android;
using Xamarin.UITest.Queries;

namespace Mavis.UITests
{
    [TestFixture]
    public class Tests
    {
        AndroidApp app;

        [SetUp]
        public void BeforeEachTest()
        {
            app = ConfigureApp.Android.StartApp();
        }

        [Test]
        public void EnteringTextAndClickingButtonShouldUpdateHeaderText()
        {
            app.EnterText(c => c.Css("#textbox"), "Test");
            app.Tap(c => c.Css("input[type=button]"));

            AppWebResult[] results = app.WaitForElement(c => c.XPath("//h1[text()=\"C# says: Test\"]"));
            app.Screenshot("Header text updated.");

            Assert.IsTrue(results.Any());
        }
    }
}
