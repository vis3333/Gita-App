package com.example.model

data class Chapter(
    val number: Int,
    val sanskritName: String,
    val englishName: String,
    val versesCount: Int,
    val summary: String
)

object GitaChapters {
    val allChapters = listOf(
        Chapter(1, "Arjuna Vishada Yoga", "The Despair of Arjuna", 47, "Arjuna encounters emotional paralysis on the battlefield of Kurukshetra, witnessing loved ones."),
        Chapter(2, "Sankhya Yoga", "Yoga of Knowledge & Eternal Soul", 72, "Krishna reveals the immortality of the Atman (Soul), the nature of duty, and the steady intellect (Sthitaprajna)."),
        Chapter(3, "Karma Yoga", "Yoga of Action", 43, "Selfless action performed without attachment to personal fruits as the path to liberation."),
        Chapter(4, "Jnana Karma Sanyasa Yoga", "Yoga of Wisdom & Renunciation of Action", 42, "The eternal descent of the Divine (Avatar) and how wisdom purifies all karma."),
        Chapter(5, "Karma Sanyasa Yoga", "Yoga of Renunciation", 29, "Comparing outer renunciation with inner detachment while actively performing duties in the world."),
        Chapter(6, "Dhyana Yoga", "Yoga of Meditation", 47, "Mastery of the restless mind through regular meditation, self-discipline, and equanimity."),
        Chapter(7, "Jnana Vijnana Yoga", "Yoga of Knowledge & Discernment", 30, "The divine and material nature of God and the four kinds of seekers who worship the Divine."),
        Chapter(8, "Aksara Brahma Yoga", "Yoga of the Imperishable Absolute", 28, "The importance of one's thoughts at the time of death and the cycle of cosmic creation."),
        Chapter(9, "Raja Vidya Raja Guhya Yoga", "Yoga of Sovereign Science & Secret", 34, "The supreme mystery of devotion, divine omnipresence, and unconditional refuge."),
        Chapter(10, "Vibhuti Yoga", "Yoga of Divine Glories", 42, "Krishna describes His divine manifestations throughout the universe and within all beings."),
        Chapter(11, "Vishwarupa Darshana Yoga", "Yoga of the Universal Cosmic Form", 55, "Arjuna is bestowed divine vision to behold Krishna's awe-inspiring cosmic form."),
        Chapter(12, "Bhakti Yoga", "Yoga of Devotion", 20, "The supreme path of loving devotion and the essential qualities of a true devotee."),
        Chapter(13, "Kshetra Kshetrajna Vibhaga Yoga", "Yoga of Field & Knower of the Field", 35, "The distinction between the physical body/nature (Kshetra) and the eternal Soul/Consciousness (Kshetrajna)."),
        Chapter(14, "Gunatraya Vibhaga Yoga", "Yoga of the Three Modes of Nature", 27, "Understanding Sattva (purity), Rajas (passion/action), and Tamas (inertia), and transcending them."),
        Chapter(15, "Purushottama Yoga", "Yoga of the Supreme Divine Person", 20, "The cosmic inverted Ashvattha tree of life and the nature of the Supreme Divine Being."),
        Chapter(16, "Daivasura Sampad Vibhaga Yoga", "Yoga of Divine & Demonic Natures", 24, "Contrasting divine virtues like fearlessness, truth, and compassion with destructive egoistic qualities."),
        Chapter(17, "Shraddhatraya Vibhaga Yoga", "Yoga of the Threefold Faith", 28, "How faith, food, sacrifice, austerity, and charity reflect the three Gunas (Sattva, Rajas, Tamas)."),
        Chapter(18, "Moksha Sanyasa Yoga", "Yoga of Liberation through Renunciation", 78, "The grand synthesis of the entire Gita: surrender of ego, selfless duty, devotion, and ultimate liberation.")
    )
}
