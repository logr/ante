package io.appkitchen.ante.core.designsystem.samples

import io.appkitchen.ante.core.designsystem.component.AnteAvatarStack
import io.appkitchen.ante.core.designsystem.component.AvatarMember

/** Spec §3.5 screenshots: 1 member, 3 members, 6 members (+3 tile), light + dark. 6 frames. */
val AvatarStackSample: ComponentSample =
    ComponentSample(
        id = "avatar_stack",
        title = "AnteAvatarStack",
        frames =
            listOf(
                SampleFrame("one_member") { AnteAvatarStack(SampleMembers.take(1)) },
                SampleFrame("three_members") { AnteAvatarStack(SampleMembers.take(3)) },
                SampleFrame("six_members") { AnteAvatarStack(SampleMembers.take(6)) },
            ),
    )

/**
 * Fixed ids, because colour is derived from the id: a golden must not depend on how a sample
 * happened to be constructed. Names cover the initials rule's cases (two words, one word).
 */
internal val SampleMembers: List<AvatarMember> =
    listOf(
        AvatarMember(id = "member-alex", name = "Alex Rivera"),
        AvatarMember(id = "member-sam", name = "Sam"),
        AvatarMember(id = "member-maya", name = "Maya Chen"),
        AvatarMember(id = "member-jordan", name = "Jordan Lee"),
        AvatarMember(id = "member-priya", name = "Priya Patel"),
        AvatarMember(id = "member-tomas", name = "Tomás Ortega"),
    )
