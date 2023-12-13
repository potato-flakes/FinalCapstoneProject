package com.system.finalcapstoneproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class NotificationActivity extends AppCompatActivity {
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_layout);

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        TextView titleTextView = findViewById(R.id.result);
        TextView definition_textview = findViewById(R.id.definition_textview);
        ImageView imageImageView = findViewById(R.id.image_image_view);

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String text = intent.getStringExtra("text");
        int imageResource = intent.getIntExtra("image", 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        titleTextView.setText(title);
        imageImageView.setImageResource(imageResource);

        if (title.contains("Forging Partnerships for a Greener Future!")){
            definition_textview.setText("Exciting moments as we sign the Memorandum of Agreement with Doc Charles Calixto, School Head of Diarabasin National High School, Sir Anthony P. Marzan, YES-O Coordinator, Atty. Vivian T. Dabu, Executive Director of KAMAI, and Engr. Rey Lucino C. De Leon, Program Officer. Together, we're paving the way for a brighter, more sustainable tomorrow.  #GreenerFuture #PartnershipForChange #Matatag #YesO #SSLG #libraryinabottle #KAMAI");
        } else if(title.contains("Polyethylene Terephthalate")){
            definition_textview.setText("Interested about Polyethylene Terephthalate? Let's jazz up the discussion about Polyethylene Terephthalate (PET)!\n" +
                    "\n" +
                    "Picture this: You're about to embark on a journey into the vibrant and fascinating world of Polyethylene Terephthalate, or as we like to call it, PET – the unsung hero of the plastic universe!\n" +
                    "\n" +
                    "PET is like the rockstar of the polymer scene. Why, you ask? Well, imagine a material that's not only lightweight but also incredibly versatile. PET is the VIP (Very Important Polymer) in the polyester family, and you've probably encountered it more times than you can count without even realizing it.\n" +
                    "\n" +
                    "PET's claim to fame is its starring role in the creation of plastic bottles. Yes, those bottles that hold your favorite fizzy drinks or that refreshing bottle of water. It's like the guardian of hydration, encased in a sleek, PET-formed vessel.\n" +
                    "\n" +
                    "But wait, there's more! PET isn't just about bottles; it's also the driving force behind those polyester threads that make your clothes oh-so-comfortable. Fashion meets functionality, all thanks to the magical touch of PET.\n" +
                    "\n" +
                    "Now, here's the sciencey bit: PET is formed through the polymerization of terephthalic acid and ethylene glycol. It's like a molecular dance where these components join forces to create a polymer powerhouse with exceptional durability and clarity.\n" +
                    "\n" +
                    "What's truly mind-blowing is PET's recycling prowess. It's like the eco-friendly superhero of plastics, ready to be transformed into new and exciting forms after its first act. It's a plastic reincarnation, if you will, contributing to the circle of sustainable life.\n" +
                    "\n" +
                    "So, there you have it – PET, the unsung rockstar of the plastic world, bringing us everything from stylish threads to reliable beverage containers. Who knew that a bunch of molecules could be so cool and versatile? Cheers to PET for keeping our drinks cool and our fashion game strong! \uD83C\uDF1F\uD83E\uDD64\uD83D\uDC57");
        } else if(title.contains("High Density Polyethylene")){
            definition_textview.setText("let's dive into the vibrant world of High Density Polyethylene (HDPE) – the tough and tenacious member of the plastic family that's as versatile as it is resilient!\n" +
                    "\n" +
                    "Imagine a material that's like the superhero of plastics – strong, durable, and ready to take on the toughest challenges. That's HDPE for you! It's the heavyweight champion of the polymer ring, known for its remarkable strength and resistance to all sorts of wear and tear.\n" +
                    "\n" +
                    "HDPE is like the silent guardian of your everyday life. Ever used a sturdy milk jug or a robust shampoo bottle? Chances are, you were holding HDPE in your hands. It's the go-to choice for packaging that needs to withstand the rigors of daily use.\n" +
                    "\n" +

                    "But HDPE isn't just about strength; it's also a bit of a chameleon. It can take on different forms and colors, adapting to whatever task you throw its way. Want a bright and colorful playground structure? HDPE's got you covered. Need a reliable piping system for water or gas? HDPE is there, making sure things flow smoothly.\n" +
                    "\n" +
                    "One of HDPE's standout features is its eco-friendly vibe. It's not just tough; it's also a friend of the environment. HDPE is fully recyclable, ready to be reborn into new products and extend its life cycle. It's like the green warrior of plastics, fighting for sustainability and a cleaner planet.\n" +
                    "\n" +
                    "Now, let's talk about the secret sauce – the polymerization process. HDPE is born when ethylene molecules decide to join forces under high pressure. It's like a molecular bonding party that results in a structure so tight, even the Hulk would envy it.\n" +
                    "\n" +
                    "In essence, HDPE is the unsung hero in the world of plastics – strong, adaptable, and committed to making our lives better. From the playground to the packaging, HDPE is the backbone that keeps things together, proving that toughness and versatility can go hand in hand. \uD83C\uDF08\uD83D\uDCAA\uD83C\uDF0D");
        } else if(title.contains("Polyvinyl Chloride Plastic Type")){
            definition_textview.setText("Let's talk about Polyvinyl Chloride (PVC), the chameleon of the plastic world – adaptable, versatile, and with a personality that ranges from rigid to flexible, depending on the situation!\n" +
                    "\n" +
                    "PVC is like the shape-shifter in the polymer universe. It can go from being a hard and sturdy material, think PVC pipes that deliver water to your home, to a soft and pliable form, like the vinyl flooring beneath your feet. It's the master of transformation, always ready to take on a new role.\n" +
                    "\n" +
                    "Now, picture a material that's not only durable but also low-maintenance. That's PVC for you! It's practically the superhero of construction, often found in pipes, window frames, and even in the wiring of your home. PVC is like the unsung architect, providing structural support and electrical pathways without stealing the spotlight.\n" +
                    "\n" +
                    "What's even more intriguing is PVC's love for color. It's like the Picasso of plastics, embracing an entire spectrum to bring vibrancy to your life. Whether it's the white picket fence in your front yard or the colorful PVC pipes in a playground, PVC knows how to add a pop of personality to the mundane.\n" +
                    "\n" +
                    "But there's a chemical ballet behind PVC's charm. It all begins with vinyl chloride molecules engaging in a polymerization dance, linking up to form the robust structure we know as PVC. It's like a molecular masterpiece, creating a material that's not only versatile but also surprisingly cost-effective.\n" +
                    "\n" +
                    "Now, let's address the sustainability question. PVC has its critics, but it's also working on its eco-friendly dance moves. Recycling initiatives are on the rise, with PVC getting a second chance at life in various products. It's like the plastic phoenix, rising from the ashes of waste to create something new.\n" +
                    "\n" +
                    "In the grand performance of polymers, PVC is the versatile artist, seamlessly transitioning from hard to soft, rigid to flexible, all while adding a splash of color to our world. It's the plastic with a personality – dynamic, resourceful, and ready for an encore. \uD83C\uDF08\uD83C\uDFE1\uD83D\uDC83");
        } else if(title.contains("Low density polyethylene")){
            definition_textview.setText("Let's delve into the world of Low Density Polyethylene (LDPE), the laid-back yet incredibly versatile member of the plastic family. Imagine a material that's like the easygoing friend you can rely on for a multitude of everyday tasks.\n" +
                    "\n" +
                    "LDPE is the mellow player in the polymer symphony, known for its flexibility and relaxed demeanor. It's the go-to plastic when you need something soft, squeezable, and easy to mold. Ever used a plastic bag at the grocery store? That's likely LDPE, giving you a handy, lightweight companion for your purchases.\n" +
                    "\n" +
                    "Picture LDPE as the silent supporter in your household – the plastic film covering your food, the cozy feel of your shampoo bottle, or the soft touch of your squeezable ketchup bottle. It's the unsung hero of packaging, providing a gentle embrace to the products we use daily.\n" +
                    "\n" +
                    "Now, let's talk about LDPE's magic trick – it's resistant to moisture. Need a container for liquids without the fear of leaks? LDPE is on the case. It's like the guardian of hydration, ensuring your drinks stay put and your sauces remain sealed.\n" +
                    "\n" +
                    "The polymerization process that gives birth to LDPE is like a chill gathering of ethylene molecules, forming a loose and laid-back structure. This laid-back structure is what gives LDPE its unique set of characteristics – flexibility, low density, and a touch of elasticity.\n" +
                    "\n" +
                    "LDPE is also a friend to the environment, with a growing emphasis on recycling initiatives. It's like the plastic with a conscience, ready to be repurposed into new and exciting forms, extending its life beyond its initial use.\n" +
                    "\n" +
                    "In the grand scheme of plastics, LDPE is the easygoing companion, adapting to various roles with a calm and collected vibe. It's the plastic that makes life a little more convenient, a bit more comfortable, and a whole lot more flexible. \uD83C\uDF3F\uD83C\uDF0D\uD83D\uDCBC");
        } else if(title.contains("Polypropolene")){
            definition_textview.setText("Let's explore the fascinating world of Polypropylene (PP), the sturdy and reliable member of the plastic family – a true workhorse with a knack for strength and versatility.\n" +
                    "\n" +
                    "Imagine a material that's like the unsung hero of your daily life, quietly supporting various tasks with its robust and resilient nature. That's PP for you! It's the dependable force behind a wide range of products, from your yogurt container to your durable outdoor furniture.\n" +
                    "\n" +
                    "PP is like the silent architect of strength, capable of handling both high and low temperatures without breaking a sweat. It's the go-to choice for containers that need to endure the heat of your microwave or the chill of your freezer. This plastic is all about durability, ensuring that your products stay intact and reliable.\n" +
                    "\n" +
                    "Now, let's talk about versatility. PP is a bit of a chameleon, adapting to different forms and functions effortlessly. Need a flexible hinge on a container? PP's got it covered. Looking for a tough and rigid material for your storage bins? Once again, PP is the answer.\n" +
                    "\n" +
                    "But what about the environment, you ask? PP is no stranger to recycling initiatives. It's like the eco-conscious warrior, ready to be reborn into new products and reduce its environmental footprint. Sustainability is key, and PP is doing its part to contribute to a greener world.\n" +
                    "\n" +
                    "The creation of PP involves a symphony of propylene molecules coming together through polymerization, forming a strong and resilient structure. It's like the chemical ballet where molecules join hands to create a material that's tough, versatile, and ready for action.\n" +
                    "\n" +
                    "In the grand ensemble of plastics, PP is the reliable performer – a durable, adaptable, and eco-friendly character that plays a crucial role in making our lives more convenient and resilient. \uD83C\uDF10\uD83D\uDD04\uD83D\uDCAA");
        } else if(title.contains("Polystyrene")){
            definition_textview.setText("Let's dive into the world of Polystyrene (PS), the lightweight and versatile member of the plastic family that's like a blank canvas, ready to take on various forms and functions.\n" +
                    "\n" +
                    "Imagine a material that's both affordable and incredibly easy to shape. That's PS for you! It's the go-to plastic when you need something lightweight, insulating, and perfect for crafting into various shapes. Ever used a foam coffee cup or seen those familiar white packaging peanuts? Chances are, you were dealing with the adaptable nature of polystyrene.\n" +
                    "\n" +
                    "Picture PS as the artist's palette of plastics, ready to be molded into intricate designs or serve as the canvas for your creative projects. It's the material that brings your packaging to life, ensuring that delicate items are snugly protected during their journey.\n" +
                    "\n" +
                    "Now, let's talk about insulation. PS has a natural talent for keeping things hot or cold. It's like the thermal guardian in your daily life, whether it's the insulating material in your disposable coffee cup or the protective layer around your electronics.\n" +
                    "\n" +
                    "The creation of PS involves the polymerization of styrene molecules, forming a structure that's both lightweight and durable. It's like the molecular symphony where simplicity meets functionality, resulting in a material that's easy to produce and incredibly versatile.\n" +
                    "\n" +
                    "But, of course, we can't ignore the environmental question. While PS has faced some criticism for its environmental impact, recycling initiatives are on the rise, giving it a chance for a second act. PS is evolving, with efforts to reduce its environmental footprint and contribute to a more sustainable future.\n" +
                    "\n" +
                    "In the grand array of plastics, PS is the versatile artist – lightweight, adaptable, and ready to take on various roles, from keeping your drinks warm to adding a layer of protection to fragile items. It's the plastic that combines simplicity with creativity, turning the ordinary into the extraordinary. \uD83C\uDFA8\uD83C\uDF0D\uD83C\uDF75");
        } else if(title.contains("Burning of plastic")){
            definition_textview.setText("Let's make the discussion about burning plastic and \"Other plastics\" more engaging:\n" +
                    "\n" +
                    "Burning of Plastic \uD83D\uDD25\n" +
                    "Imagine plastic undergoing a fiery transformation – that's the dramatic act of burning! When plastic meets the flames, it's not just a simple disappearing act; it's a chemical spectacle with both awe and environmental caution.\n" +
                    "\n" +
                    "\uD83D\uDD25 The Blaze Ballet:\n" +
                    "Burning plastic is like a choreography of molecules, dancing in the heat. The vibrant colors in the flames tell a story of the various components within the plastic, each contributing to the fiery performance. It's a chemical ballet on the stage of combustion.\n" +
                    "\n" +
                    "⚠️ Caution Lights:\n" +
                    "While the flames may be mesmerizing, the environmental red flags start waving. Burning plastic releases a cocktail of toxic substances into the air. It's not just about the disappearing act; it's about the hazardous encore, leaving behind residues that can harm both the atmosphere and our health.\n" +
                    "\n" +
                    "\uD83C\uDF0D The Earth's Reaction:\n" +
                    "Mother Earth watches this fiery drama with a concerned gaze. The release of harmful pollutants contributes to air pollution and can even lead to the formation of acid rain. It's a reminder that our actions, even with seemingly small pieces of plastic, can have significant consequences on the planet's well-being.\n" +
                    "\n" +
                    "\uD83D\uDCA1 The Sustainable Spotlight:\n" +
                    "The burning of plastic nudges us to shine the spotlight on sustainable practices. Instead of resorting to burning, we can explore eco-friendly alternatives like recycling or repurposing. It's a call for a greener script, where plastic takes on a new role in the cycle of reuse rather than contributing to environmental harm.");
        } else if(title.contains("Other plastics")){
            definition_textview.setText("When it comes to \"Other plastics,\" we step into a realm of possibilities and variety. It's like a plastic carnival with different types, each bringing its own flair to the polymer party.\n" +
                    "\n" +
                    "\uD83C\uDF89 Plastic Diversity:\n" +
                    "\"Other plastics\" is a celebration of diversity. From the tough and sturdy to the lightweight and flexible, these plastics are the characters in the grand play of materials. It's a reminder that plastics aren't a monolithic entity; they're a vibrant family with unique personalities.\n" +
                    "\n" +
                    "\uD83C\uDF10 Endless Possibilities:\n" +
                    "Each type of plastic under the \"Other\" category has its own set of capabilities. They can be molded into intricate shapes, serve as protective layers, or even become the canvas for creative endeavors. It's a reminder that plastics, when used responsibly, can be versatile allies in our daily lives.\n" +
                    "\n" +
                    "\uD83D\uDD04 Recycling Resilience:\n" +
                    "The story doesn't end with their first act. \"Other plastics\" embrace the concept of recycling, showcasing resilience and adaptability. They can be reborn into new products, reducing waste and contributing to a circular economy. It's a plastic evolution, where each type gets a chance for a sustainable encore.\n" +
                    "\n" +
                    "\uD83D\uDE80 Future Innovations:\n" +
                    "As we explore \"Other plastics,\" we catch a glimpse of the future. Innovations in plastic technology are ongoing, with researchers and scientists working on biodegradable options and environmentally friendly alternatives. It's a sneak peek into a plastic future that aligns with ecological harmony.\n" +
                    "\n" +
                    "In the grand narrative of plastics, whether dealing with the fiery drama of burning or the diverse world of \"Other plastics,\" each chapter carries a lesson. It's an invitation to rethink our relationship with plastics, encouraging a storyline that leads to a more sustainable and responsible future. \uD83D\uDD17\uD83C\uDF31");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
